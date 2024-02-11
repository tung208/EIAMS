package EIAMS.services.interfaces;

import EIAMS.entities.Student;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface StudentServiceInterface {
     Page<Student> list(Integer page, Integer limit);
     List<Student> list();
     void create(Student student);
     void update(int id, Student student);
     Optional<Student> getStudentDetail(int id);
     void delete(int id);
//     void exportListStudent(List<Student> students, String filePath) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException;
     void importListStudent(MultipartFile file) throws IOException;
}
