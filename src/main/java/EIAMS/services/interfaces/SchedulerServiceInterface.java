package EIAMS.services.interfaces;

import EIAMS.dtos.SchedulerDetailDto;
import EIAMS.entities.Scheduler;
import EIAMS.entities.Student;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface SchedulerServiceInterface {
    void arrangeStudent(int semesterId) throws Exception;
    void setExamCode(int semesterId);
    void arrangeLecturer(int semesterId);
    List<List<String>> list(Integer semesterId, String search, String startDate, String endDate);
    Page<Student> getListStudentInARoom(Integer schedulerId,String search, Integer page, Integer limit);
    List<SchedulerDetailDto> getListSchedulerBySubjectCode(Integer semesterId, String subjectCode);
}
