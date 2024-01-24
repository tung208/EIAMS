package EIAMS.services.interfaces;

import EIAMS.dtos.StudentDto;
import EIAMS.entities.Student;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface StudentServiceInterface {
     Page<Student> list(Integer page, Integer limit);
     List<Student> list();
     void create(StudentDto dto);
     void update(int id, StudentDto dto);
     Optional<Student> getStudentDetail(int id);
     void delete(int id);
     void exportListStudent(List<Student> students, String filePath);
     void importListStudent(MultipartFile file) throws IOException;
}
