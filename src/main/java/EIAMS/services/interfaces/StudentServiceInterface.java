package EIAMS.services.interfaces;

import EIAMS.dtos.StudentDto;
import EIAMS.dtos.StudentSubjectDto;
import EIAMS.entities.Semester;
import EIAMS.entities.Student;
import EIAMS.entities.StudentSubject;
import EIAMS.exception.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StudentServiceInterface {
     Page<Student> list(String search, String memberCode, Integer page, Integer limit);

     List<Student> list();
     void create(StudentDto student);
     void update(StudentDto studentDto);
     Optional<Student> getStudentDetail(int id);
     void delete(int id);

     void saveCustomersToDatabase(MultipartFile file);
     Integer uploadStudents(MultipartFile file, int semester_id) throws IOException;

     Integer uploadCMND(MultipartFile file, int semester_id) throws IOException;

     Integer uploadBlackList(MultipartFile file, int semester_id) throws IOException;

     Page<Student> search(Integer page, Integer limit, String rollNumber, String memberCode, String fullName, String cmtnd);

     Page<StudentSubject> searchStudentSubject(Integer page, Integer limit, Integer semesterId, String rollNumber, String subjectCode, String groupName, Integer backList);

     void updateStudentSubject(int id, StudentSubjectDto studentSubjectDto);

     void deleteStudentSubject(int id);

     void createStudentSubject(StudentSubjectDto studentSubjectDto) throws EntityNotFoundException;
}
