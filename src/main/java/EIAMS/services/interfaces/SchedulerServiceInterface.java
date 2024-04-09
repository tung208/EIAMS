package EIAMS.services.interfaces;

import EIAMS.dtos.SchedulerDetailDto;
import EIAMS.dtos.StudentScheduleDto;
import EIAMS.entities.Room;
import EIAMS.entities.Scheduler;

import java.util.List;

public interface SchedulerServiceInterface {
    void arrangeStudent(int semesterId) throws Exception;
    void setExamCode(int semesterId) throws Exception;
    void arrangeLecturer(int semesterId);
    List<Room> list(String search, String startDate, String endDate, String lecturerId);
    List<Scheduler> listSchedulerByRoom(int roomId, String startDate, String endDate, String lecturerId);
    List<StudentScheduleDto> getListStudentInARoom(Integer schedulerId, String search);
    List<SchedulerDetailDto> getListSchedulerBySubjectCode(Integer semesterId, String subjectCode);
    void updateLecturer(int schedulerId, int lecturerId) throws Exception;
    void swapLecturer(int schedulerId, int schedulerSwapId) throws Exception;
    SchedulerDetailDto get(int schedulerId);

}
