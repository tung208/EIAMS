package EIAMS.services.interfaces;

import EIAMS.dtos.SchedulerDetailDto;
import EIAMS.entities.Room;
import EIAMS.entities.Scheduler;
import EIAMS.entities.Student;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;

import java.util.List;

public interface SchedulerServiceInterface {
    void arrangeStudent(int semesterId) throws Exception;
    void setExamCode(int semesterId) throws Exception;
    void arrangeLecturer(int semesterId);
    void updateLecturer(int schedulerId, int lecturerId);
    List<Room> list(String search, String startDate, String endDate);
    List<Scheduler> listSchedulerByRoom(int roomId, String startDate, String endDate);
    Page<Student> getListStudentInARoom(Integer schedulerId,String search, Integer page, Integer limit);
    List<SchedulerDetailDto> getListSchedulerBySubjectCode(Integer semesterId, String subjectCode);
    void swapLecturer(int schedulerId, int schedulerSwapId) throws Exception;
}
