package EIAMS.services.interfaces;

import EIAMS.entities.Scheduler;
import EIAMS.entities.Student;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public interface SchedulerServiceInterface {
    void arrangeStudent(int semesterId) throws Exception;
    Page<Scheduler> list(Integer semesterId, String search,String startDate, String endDate, Integer page, Integer limit);
    Page<Student> getListStudentInARoom(Integer schedulerId,String search, Integer page, Integer limit);
    List<Scheduler> getListSchedulerBySubjectCode(Integer semesterId, String subjectCode);
}
