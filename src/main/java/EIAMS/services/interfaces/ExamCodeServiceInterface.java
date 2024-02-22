package EIAMS.services.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ExamCodeServiceInterface {
    Integer uploadExamCode(MultipartFile file, int semester_id) throws IOException;
}
