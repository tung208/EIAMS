package EIAMS.services.interfaces;

import EIAMS.entities.Semester;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ExamCodeServiceInterface {
    Integer uploadExamCode(MultipartFile file, int semester_id) throws IOException;
    Page<Semester> search(Integer page, Integer limit, Integer semesterId , String subjectCode);
}
