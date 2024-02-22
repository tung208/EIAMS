package EIAMS.services.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface SubjectServiceInterface {
    Integer uploadSubject(MultipartFile file, int semester_id) throws IOException;
    Integer uploadSubjectNoLab(MultipartFile file, int semester_id) throws IOException;
    Integer uploadSubjectDontMix(MultipartFile file, int semester_id) throws IOException;
}
