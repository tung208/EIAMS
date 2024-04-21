package EIAMS.services.interfaces;

import EIAMS.dtos.SemesterDto;
import EIAMS.entities.Semester;
import EIAMS.exception.EntityNotFoundException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SemesterServiceInterface {
    Page<Semester> list(Integer page, Integer limit);
    Page<Semester> search(Integer page, Integer limit, String name, String code);
    List<Semester> list();
    Semester create(SemesterDto semesterDto);
    Semester update(int id, SemesterDto semesterDto) throws EntityNotFoundException;
    void delete(int id);

}
