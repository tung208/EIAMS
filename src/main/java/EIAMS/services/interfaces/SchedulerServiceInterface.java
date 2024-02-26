package EIAMS.services.interfaces;

public interface SchedulerServiceInterface {
    long countStudentBySubject(int semesterId, String subjectCode);

    void arrangeStudent(int semesterId);
}
