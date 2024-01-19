package EIAMS.services.interfaces;

import EIAMS.dtos.StudentDto;
import EIAMS.entities.Student;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;

public interface StudentServiceInterface {
     Page<Student> list(Integer page, Integer limit);
     List<Student> list();
     void update(int id, StudentDto dto);
     void delete(int id);
     void exportListStudent(List<Student> students, String filePath);
     void importListStudent(String filePath) throws IOException;
}
