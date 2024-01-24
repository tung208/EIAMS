package EIAMS.services.interfaces;

import EIAMS.dtos.SemesterDto;
import EIAMS.entities.Semester;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SemesterServiceInterface {
    Page<Semester> list(Integer page, Integer limit);
    List<Semester> list();
    void update(int id, SemesterDto dto);
    void delete(int id);

    void exportListSemester(List<Semester> students, String filePath);
    void importListSemester(String filePath);
}
