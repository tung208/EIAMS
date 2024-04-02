package EIAMS.services.interfaces;

import EIAMS.dtos.ExamCodeDto;
import EIAMS.entities.ExamCode;
import EIAMS.entities.Semester;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ExamCodeServiceInterface {
    Integer uploadExamCode(MultipartFile file, int semester_id) throws IOException;
    Page<ExamCode> search(Integer page, Integer limit, Integer semesterId , String subjectCode);

    ExamCode create(ExamCodeDto examCodeDto);

    void update(ExamCodeDto examCodeDto);

    void delete(Integer id);
}
