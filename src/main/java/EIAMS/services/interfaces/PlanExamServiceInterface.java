package EIAMS.services.interfaces;

import EIAMS.dtos.PlanExamDto;
import EIAMS.entities.PlanExam;
import EIAMS.exception.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;

public interface PlanExamServiceInterface {
    Integer uploadPlanExam(MultipartFile file, int semester_id, String type) throws IOException, ParseException;
    Page<PlanExam> search(Integer page, Integer limit, Integer semesterId , String subjectCode);

    PlanExam create(PlanExamDto planExamDto);

    PlanExam update(int id, PlanExamDto planExamDto ) throws EntityNotFoundException;

    PlanExam delete(int id) throws EntityNotFoundException;

    void deleteSemesterId(int id);
}
