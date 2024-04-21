package EIAMS.services.interfaces;

import EIAMS.dtos.LecturerDto;
import EIAMS.entities.Lecturer;
import EIAMS.exception.EntityExistException;
import EIAMS.exception.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface LecturerServiceInterface {
    Integer uploadLecturer(MultipartFile file, int semester_id) throws IOException;

    Page<Lecturer> search(Integer page, Integer limit, Integer semesterId , String email, String examSubject, int totalSlot);

    Lecturer create(LecturerDto lecturerDto) throws EntityExistException;

    Lecturer update(int id, LecturerDto lecturerDto) throws EntityNotFoundException, EntityExistException;
    Lecturer delete(int id) throws EntityExistException;
}
