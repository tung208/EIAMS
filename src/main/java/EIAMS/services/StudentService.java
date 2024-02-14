package EIAMS.services;

import EIAMS.entities.Semester;
import EIAMS.entities.Student;
import EIAMS.entities.csvRepresentation.DSSVCsvRepresentation;
import EIAMS.helper.Pagination;
import EIAMS.repositories.SemesterRepository;
import EIAMS.repositories.StudentRepository;
import EIAMS.services.interfaces.StudentServiceInterface;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService implements StudentServiceInterface {

    private final StudentRepository studentRepository;
    private final SemesterRepository semesterRepository;
    private final Pagination pagination;
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);
    @Autowired
    private TransactionTemplate transactionTemplate;

//    @Autowired
//    private Job exportCsvJob;

    @Override
    public Page<Student> list(Integer page, Integer limit) {
        Pageable pageable = pagination.getPageable(page, limit);
        return studentRepository.findAll(pageable);
    }

    @Override
    public List<Student> list() {
        return studentRepository.findAll();
    }

    @Override
    public void create(Student student) {
        studentRepository.save(student);
    }

    @Override
    public void update(int id, Student student) {
        Optional<Student> s = studentRepository.findById(id);
        if (s.isPresent()) {
            Student studentUpdate = s.get();
            studentUpdate.setRollNumber(student.getRollNumber());
            studentUpdate.setFullName(student.getFullName());
            studentUpdate.setCmtnd(student.getCmtnd());
            studentUpdate.setMemberCode(student.getMemberCode());
            studentRepository.save(studentUpdate);
        }
    }

    @Override
    public Optional<Student> getStudentDetail(int id) {
        return studentRepository.findById(id);
    }

    @Override
    public void delete(int id) {
        studentRepository.deleteById(id);
    }

//    @Override
//    public void exportListStudent(List<Student> students, String filePath) {
//        // Create a JobParameters with the file path as a parameter
//        Map<String, JobParameter> parameters = new HashMap<>();
//        parameters.put("filePath", new JobParameter(filePath));
//
//        JobParameters jobParameters = new JobParameters(parameters);
//
//        // Create a StepExecutionListener to set the list of students as a JobExecutionParameter
//        StepExecutionListener stepExecutionListener = new StepExecutionListener() {
//            @Override
//            public void beforeStep(StepExecution stepExecution) {
//                ExecutionContext executionContext = stepExecution.getExecutionContext();
//                executionContext.put("students", students);
//            }
//
//            @Override
//            public ExitStatus afterStep(StepExecution stepExecution) {
//                return null;
//            }
//        };
//
//        // Launch the export job
//        try {
//            JobExecution jobExecution = jobLauncher.run(exportCsvJob, jobParameters);
//
//            // Optional: Monitor job execution if needed
//            BatchStatus batchStatus = jobExecution.getStatus();
//            if (batchStatus != BatchStatus.COMPLETED) {
//                // Handle job failure or other statuses
//            }
//        } catch (Exception e) {
//            // Handle job launching exception
//            e.printStackTrace();
//        }
//    }

    @Override
    public void saveCustomersToDatabase(MultipartFile file){

    }

    @Override
    public Integer uploadStudents(MultipartFile file) throws IOException {
        List<Student> students = parseCsv(file);

        int corePoolSize = 5;
        int maximumPoolSize = 10;
        long keepAliveTime = 60L;
        int queueCapacity = 100;
        // Tạo một ThreadPoolExecutor với các tham số đã cho
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueCapacity)); // Hàng đợi dùng để lưu trữ các nhiệm vụ chưa được thực hiện
        for (int i = 0; i < 20; i++) {
            int finalI = i;
//            executor.execute(()-> {
//                System.out.println("Task " + finalI + " executed by thread: " + Thread.currentThread().getName());
//            });
            executor.execute(new TestThread(i));
        }
//        System.out.println(students.size());
        return students.size();
    }

    private void saveStudent(List<Student> students){
        for (Student element : students) {
            try{
                studentRepository.save(element);
            } catch (DataIntegrityViolationException e){
//                e.printStackTrace();
            }
        }
    }
    private List<Student> parseCsv(MultipartFile file) throws IOException {
        try(Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            HeaderColumnNameMappingStrategy<DSSVCsvRepresentation> strategy =
                    new HeaderColumnNameMappingStrategy<>();
            strategy.setType(DSSVCsvRepresentation.class);
            CsvToBean<DSSVCsvRepresentation> csvToBean =
                    new CsvToBeanBuilder<DSSVCsvRepresentation>(reader)
                            .withMappingStrategy(strategy)
                            .withIgnoreEmptyLine(true)
                            .withIgnoreLeadingWhiteSpace(true)
                            .build();
            return csvToBean.parse()
                    .stream()
                    .map(csvLine -> Student.builder()
                            .rollNumber(csvLine.getRollNumber())
                            .memberCode(csvLine.getMemberCode())
                            .fullName(csvLine.getFullName())
                            .build()
                    )
                    .collect(Collectors.toList());
        }
    }
}
