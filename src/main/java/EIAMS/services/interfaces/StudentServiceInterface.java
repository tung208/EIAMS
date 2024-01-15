package EIAMS.services.interfaces;

import EIAMS.entities.Student;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StudentServiceInterface {
     Page<Student> list(Integer page, Integer limit);
     List<Student> list();
}
