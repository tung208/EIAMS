package EIAMS.services.interfaces;

import EIAMS.entities.Scheduler;
import EIAMS.entities.Student;
import org.springframework.data.domain.Page;

public interface SchedulerServiceInterface {
    void arrangeStudent(int semesterId);
    Page<Scheduler> list(Integer semesterId,String search, Integer page, Integer limit);
    Page<Student> getListStudentInARoom(Integer schedulerId,String search, Integer page, Integer limit);
}
