package EIAMS.services.interfaces;

import EIAMS.dtos.SubjectDto;
import EIAMS.entities.Subject;
import EIAMS.exception.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface SubjectServiceInterface {
    Integer uploadSubject(MultipartFile file, int semester_id) throws IOException;
    Integer uploadSubjectNoLab(MultipartFile file, int semester_id) throws IOException;
    Integer uploadSubjectDontMix(MultipartFile file, int semester_id) throws IOException;
    Page<Subject> search(Integer page, Integer limit, Integer semesterId , String name, String code);

    Subject update(int id, SubjectDto subjectDto) throws EntityNotFoundException;
    void delete(int id);

    Subject create(SubjectDto subjectDto);

    void deleteSemesterId(int id);
    List<Subject> getSubjectDontMix(int semesterId, String dontMix);
}
