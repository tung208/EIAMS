package EIAMS.services.interfaces;

import EIAMS.dtos.SemesterDto;
import EIAMS.entities.Semester;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SemesterServiceInterface {
    Page<Semester> list(Integer page, Integer limit);
    List<Semester> list();
    Semester create(SemesterDto semesterDto);
    void update(int id, Semester semester);
    void delete(int id);

    void exportListSemester(List<Semester> students, String filePath);
    void importListSemester(MultipartFile file);
}
