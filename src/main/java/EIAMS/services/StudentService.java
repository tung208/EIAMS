package EIAMS.services;

import EIAMS.entities.Semester;
import EIAMS.entities.Student;
import EIAMS.helper.Pagination;
import EIAMS.repositories.SemesterRepository;
import EIAMS.repositories.StudentRepository;
import EIAMS.services.interfaces.StudentServiceInterface;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StudentService implements StudentServiceInterface {
    private final StudentRepository studentRepository;
    private final SemesterRepository semesterRepository;
    private final Pagination pagination;
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job exportCsvJob;

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
            studentUpdate.setSubjectCode(student.getSubjectCode());
            studentUpdate.setFullName(student.getFullName());
            studentUpdate.setSemesterId(student.getSemesterId());
            studentUpdate.setCmtnd(student.getCmtnd());
            studentUpdate.setMemberCode(student.getMemberCode());
            studentUpdate.setBlackList(student.getBlackList());
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

    @Override
    public void exportListStudent(List<Student> students, String filePath) {
        // Create a JobParameters with the file path as a parameter
        Map<String, JobParameter> parameters = new HashMap<>();
        parameters.put("filePath", new JobParameter(filePath));

        JobParameters jobParameters = new JobParameters(parameters);

        // Create a StepExecutionListener to set the list of students as a JobExecutionParameter
        StepExecutionListener stepExecutionListener = new StepExecutionListener() {
            @Override
            public void beforeStep(StepExecution stepExecution) {
                ExecutionContext executionContext = stepExecution.getExecutionContext();
                executionContext.put("students", students);
            }

            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {
                return null;
            }
        };

        // Launch the export job
        try {
            JobExecution jobExecution = jobLauncher.run(exportCsvJob, jobParameters);

            // Optional: Monitor job execution if needed
            BatchStatus batchStatus = jobExecution.getStatus();
            if (batchStatus != BatchStatus.COMPLETED) {
                // Handle job failure or other statuses
            }
        } catch (Exception e) {
            // Handle job launching exception
            e.printStackTrace();
        }
    }

    @Override
    public void importListStudent(MultipartFile file) throws IOException {

    }
}
