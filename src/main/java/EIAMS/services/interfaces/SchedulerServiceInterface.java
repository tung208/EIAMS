package EIAMS.services.interfaces;

import EIAMS.dtos.SchedulerDetailDto;
import EIAMS.entities.Student;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SchedulerServiceInterface {
    void arrangeStudent(int semesterId) throws Exception;
    void setExamCode(int semesterId);
    void arrangeLecturer(int semesterId);
    void updateLecturer(int schedulerId, int lecturerId);
    List<List<String>> list(String search, String startDate, String endDate);
    Page<Student> getListStudentInARoom(Integer schedulerId,String search, Integer page, Integer limit);
    List<SchedulerDetailDto> getListSchedulerBySubjectCode(Integer semesterId, String subjectCode);
    void swapLecturer(int schedulerId, int schedulerSwapId);
}
