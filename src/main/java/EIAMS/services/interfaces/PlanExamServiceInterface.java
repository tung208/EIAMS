package EIAMS.services.interfaces;

import EIAMS.entities.Semester;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;

public interface PlanExamServiceInterface {
    Integer uploadPlanExam(MultipartFile file, int semester_id, String type) throws IOException, ParseException;
    Page<Semester> search(Integer page, Integer limit, Integer semesterId , String subjectCode);
}
