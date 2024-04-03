package EIAMS.services.interfaces;

import EIAMS.dtos.LecturerDto;
import EIAMS.entities.ExamCode;
import EIAMS.entities.Lecturer;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface LecturerServiceInterface {
    Integer uploadLecturer(MultipartFile file, int semester_id) throws IOException;

    Page<Lecturer> search(Integer page, Integer limit, Integer semesterId , String email, String examSubject, int totalSlot);

    Lecturer create(LecturerDto lecturerDto);

    void update(int id,LecturerDto lecturerDto);
    void delete(int id);
}