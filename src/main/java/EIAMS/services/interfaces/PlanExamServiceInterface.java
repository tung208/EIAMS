package EIAMS.services.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;

public interface PlanExamServiceInterface {
    Integer uploadPlanExam(MultipartFile file, int semester_id, String type) throws IOException, ParseException;
}
