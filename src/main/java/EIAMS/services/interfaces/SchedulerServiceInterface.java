package EIAMS.services.interfaces;

import EIAMS.entities.Scheduler;
import EIAMS.entities.Student;
import org.springframework.data.domain.Page;

public interface SchedulerServiceInterface {
    void arrangeStudent(int semesterId);

    Page<Scheduler> list(String search, Integer page, Integer limit);
}
