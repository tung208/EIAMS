package EIAMS.services.interfaces;

import EIAMS.entities.Semester;
import EIAMS.entities.Student;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StudentServiceInterface {
     Page<Student> list(String search, String memberCode, Integer page, Integer limit);
     List<Student> list();
     void create(Student student);
     void update(int id, Student student);
     Optional<Student> getStudentDetail(int id);
     void delete(int id);
//     void exportListStudent(List<Student> students, String filePath) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException;

     void saveCustomersToDatabase(MultipartFile file);
     Integer uploadStudents(MultipartFile file, int semester_id) throws IOException;

     Integer uploadCMND(MultipartFile file, int semester_id) throws IOException;

     Integer uploadBlackList(MultipartFile file, int semester_id) throws IOException;

     Page<Student> search(Integer page, Integer limit, String rollNumber, String memberCode, String fullName, String cmtnd);
}
