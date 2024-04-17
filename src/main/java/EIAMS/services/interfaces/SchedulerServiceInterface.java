package EIAMS.services.interfaces;

import EIAMS.dtos.RoomScheduleDto;
import EIAMS.dtos.ScheduleToSwapDto;
import EIAMS.dtos.SchedulerDetailDto;
import EIAMS.dtos.StudentScheduleDto;

import java.util.List;

public interface SchedulerServiceInterface {
    void arrangeStudent(int semesterId) throws Exception;
    void setExamCode(int semesterId) throws Exception;
    void arrangeLecturer(int semesterId) throws Exception;
    List<RoomScheduleDto> list(Integer semesterId, String search, String startDate, String endDate, String lecturerId);
    List<SchedulerDetailDto> listSchedulerByRoom(Integer semesterId, int roomId, String startDate, String endDate, String lecturerId);
    List<StudentScheduleDto> getListStudentInARoom(Integer schedulerId, String search);
    List<SchedulerDetailDto> getListSchedulerBySubjectCode(Integer semesterId, String subjectCode);
    void updateLecturer(int schedulerId, int lecturerId) throws Exception;
    void swapLecturer(int schedulerId, int schedulerSwapId) throws Exception;
    SchedulerDetailDto get(int schedulerId);
    List<Integer> getIdsByTimeRange(Integer semesterId, String startDate, String endDate);
    List<ScheduleToSwapDto> getListByTimeRange(Integer semesterId, String startDate, String endDate);

}
