package EIAMS.services;

import EIAMS.dtos.StudentDto;
import EIAMS.dtos.StudentSubjectDto;
import EIAMS.entities.Student;
import EIAMS.entities.StudentSubject;
import EIAMS.entities.csvRepresentation.BlackListRepresentation;
import EIAMS.entities.csvRepresentation.CMNDCsvRepresentation;
import EIAMS.entities.csvRepresentation.DSSVCsvRepresentation;
import EIAMS.exception.EntityNotFoundException;
import EIAMS.helper.Pagination;
import EIAMS.repositories.SemesterRepository;
import EIAMS.repositories.StudentRepository;
import EIAMS.repositories.StudentSubjectRepository;
import EIAMS.services.excel.ExcelBlackList;
import EIAMS.services.excel.ExcelCMND;
import EIAMS.services.excel.ExcelDSSV;
import EIAMS.services.interfaces.StudentServiceInterface;
import EIAMS.services.thread.SaveCMND;
import EIAMS.services.thread.SaveStudent;
import EIAMS.services.thread.SaveStudentSubject;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService implements StudentServiceInterface {

    private final StudentRepository studentRepository;
    private final StudentSubjectRepository studentSubjectRepository;
    private final Pagination pagination;
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);


    @Override
    public Page<Student> list(String search, String memberCode, Integer page, Integer limit) {
        Pageable pageable = pagination.getPageable(page, limit);
        return studentRepository.findAllByRollNumberContainingIgnoreCaseOrFullNameContainingIgnoreCaseAndMemberCodeContainingIgnoreCase(
                search, search, memberCode, pageable);
    }

    @Override
    public Page<Student> search(Integer page, Integer limit, String rollNumber, String memberCode, String fullName, String cmtnd) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("id").descending());
        return studentRepository.findByDynamic(rollNumber, memberCode, fullName, cmtnd, pageable);
    }

    @Override
    public Page<StudentSubject> searchStudentSubject(Integer page, Integer limit, Integer semesterId, String rollNumber, String subjectCode, String groupName, Integer backList) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("id").descending());
        return studentSubjectRepository.findByDynamic(semesterId, rollNumber, subjectCode, groupName, backList, pageable);
    }

    @Override
    public List<Student> list() {
        return studentRepository.findAll();
    }

    @Override
    public void create(StudentDto studentDto) {
        Student student = Student.builder()
                .rollNumber(studentDto.getRollNumber())
                .memberCode(studentDto.getMemberCode())
                .fullName(studentDto.getFullName())
                .cmtnd(studentDto.getCmtnd())
                .build();
        studentRepository.save(student);
    }

    @Override
    public void update(StudentDto student) {
        Optional<Student> s = studentRepository.findById(student.getId());
        if (s.isPresent()) {
            Student studentUpdate = Student.builder()
                    .id(student.getId())
                    .rollNumber(student.getRollNumber())
                    .memberCode(student.getMemberCode())
                    .fullName(student.getFullName())
                    .cmtnd(student.getCmtnd())
                    .build();
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
    public void saveCustomersToDatabase(MultipartFile file){

    }

    @Override
    @Transactional
    public Integer uploadStudents(MultipartFile file, int semester_id) throws IOException {
        List<DSSVCsvRepresentation> dssvCsvRepresentations = new ExcelDSSV().getDataFromExcel(file.getInputStream());

        Map<String,Student> students = new HashMap<>();
        List<StudentSubject> studentSubjects = new ArrayList<>();

        int corePoolSize = 10;
        int maximumPoolSize = 15;
        long keepAliveTime = 60L;
        int queueCapacity = 100;
        // Tạo một ThreadPoolExecutor với các tham số đã cho
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueCapacity));
        // Hàng đợi dùng để lưu trữ các nhiệm vụ chưa được thực hiện

        // Kích thước của danh sách con
        int sublistSize = 2000;

        for (DSSVCsvRepresentation element: dssvCsvRepresentations) {
            try {
                Student student = Student.builder()
                        .rollNumber(element.getRollNumber().toUpperCase().trim())
                        .memberCode(element.getMemberCode().toUpperCase().trim())
                        .fullName(element.getFullName().trim())
                        .build();
                students.put(element.getRollNumber().toUpperCase().trim(), student);

                StudentSubject studentSubject = StudentSubject.builder()
                        .semesterId(semester_id)
                        .rollNumber(element.getRollNumber().toUpperCase().trim())
                        .subjectCode(element.getSubjectCode().toUpperCase().trim())
                        .groupName(element.getGroupName().toUpperCase().trim())
                        .build();
                studentSubjects.add(studentSubject);
            } catch (Exception e){
                System.out.println(element.getRollNumber());
            }
        }


        List<Student> listStudent = students.values().stream().collect(Collectors.toList());
        List<String> listKeyStudent = new ArrayList<>(students.keySet());

        System.out.println("size of dssv: "+dssvCsvRepresentations.size());
        System.out.println("list student: " + listStudent.size());

        studentRepository.deleteByRollNumbers(listKeyStudent);
        for (int i = 0; i < listStudent.size(); i += sublistSize) {
            int endIndex = Math.min(i + sublistSize, listStudent.size());
            List<Student> sublistStudent = listStudent.subList(i, endIndex);
            executor.execute(new SaveStudent(sublistStudent,semester_id,studentRepository,i));
        }

        System.out.println("list student subject: " + studentSubjects.size());
        studentSubjectRepository.deleteBySemesterId(semester_id);
        for (int i = 0; i < studentSubjects.size(); i += sublistSize) {
            int endIndex = Math.min(i + sublistSize, studentSubjects.size());
            List<StudentSubject> sublist = studentSubjects.subList(i, endIndex);
            executor.execute(new SaveStudentSubject(sublist,studentSubjectRepository,i));
        }

        return dssvCsvRepresentations.size();
    }

    @Override
    @Transactional
    public Integer uploadCMND(MultipartFile file, int semester_id) throws IOException {
        List<CMNDCsvRepresentation> cmndCsvRepresentations = new ExcelCMND().getDataFromExcel(file.getInputStream());
        Map<String,Student> students = new HashMap<>();

        int corePoolSize = 10;
        int maximumPoolSize = 15;
        long keepAliveTime = 60L;
        int queueCapacity = 100;
        // Tạo một ThreadPoolExecutor với các tham số đã cho
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueCapacity));
        // Hàng đợi dùng để lưu trữ các nhiệm vụ chưa được thực hiện

        // Kích thước của danh sách con
        int sublistSize = 2000;

        for (CMNDCsvRepresentation element: cmndCsvRepresentations) {
            Student student = Student.builder()
                    .rollNumber(element.getRollNumber().toUpperCase().trim())
                    .cmtnd(element.getCmtnd().trim())
                    .build();
            students.put(element.getRollNumber().toUpperCase().trim(), student);
        }


        List<Student> listStudent = students.values().stream().collect(Collectors.toList());
        for (int i = 0; i < listStudent.size(); i += sublistSize) {
            int endIndex = Math.min(i + sublistSize, listStudent.size());
            List<Student> sublistStudent = listStudent.subList(i, endIndex);
            executor.execute(new SaveCMND(sublistStudent,studentRepository));
        }

        return cmndCsvRepresentations.size();
    }

    @Override
    @Transactional
    public Integer uploadBlackList(MultipartFile file, int semester_id) throws IOException {
        List<BlackListRepresentation> blackListRepresentations = new ExcelBlackList().getDataFromExcel(file.getInputStream());

        // Kích thước của danh sách con
        // Tạo một Pageable với limit là 1
        Pageable pageable = PageRequest.of(0, 1);
        int sublistSize = 500;
        for (BlackListRepresentation element: blackListRepresentations) {
            List<StudentSubject> studentSubjectList = studentSubjectRepository.findByRollNumberAndGroupNameAndSemesterId(
                    safeTrim(element.getRollNumber(),1),
                    safeTrim(element.getBlackList(),1),
                    semester_id
            );
            if (studentSubjectList.size() != 0){
                for (StudentSubject item: studentSubjectList){
                    item.setBlackList(1);
                    studentSubjectRepository.save(item);
                }
            }
        }

        return null;
    }

    public static String safeTrim(String str,int mode) {
        if (mode == 1){
            return str == null ? null : str.toUpperCase().trim();
        } else {
            return str == null ? null : str.trim();
        }
    }

    @Override
    public void updateStudentSubject(int id, StudentSubjectDto studentSubjectDto) {
        Optional<StudentSubject> ss = studentSubjectRepository.findById(id);
        Optional<Student> student = studentRepository.findByRollNumber(studentSubjectDto.getRollNumber());
        if (ss.isPresent() && student.isPresent()) {
            StudentSubject studentSubject = StudentSubject.builder()
                    .semesterId(studentSubjectDto.getSemesterId())
                    .rollNumber(studentSubjectDto.getRollNumber())
                    .subjectCode(studentSubjectDto.getSubjectCode())
                    .groupName(studentSubjectDto.getGroupName())
                    .blackList(studentSubjectDto.getBlackList())
                        .build();
            studentSubjectRepository.save(studentSubject);
        }
    }

    @Override
    public void deleteStudentSubject(int id) {
        studentSubjectRepository.deleteById(id);
    }

    @Override
    public void createStudentSubject(StudentSubjectDto studentSubjectDto) throws EntityNotFoundException {
        Optional<Student> student = studentRepository.findByRollNumber(studentSubjectDto.getRollNumber());
        if (student.isPresent()) {
            StudentSubject studentSubject = StudentSubject.builder()
                    .semesterId(studentSubjectDto.getSemesterId())
                    .rollNumber(studentSubjectDto.getRollNumber())
                    .subjectCode(studentSubjectDto.getSubjectCode())
                    .groupName(studentSubjectDto.getGroupName())
                    .blackList(studentSubjectDto.getBlackList())
                    .build();
        } else {
            // Trả ra exception
            throw new EntityNotFoundException("Not found student");
        }
    }
}
