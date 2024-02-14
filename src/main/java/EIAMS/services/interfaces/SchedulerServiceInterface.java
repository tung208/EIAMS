package EIAMS.services.interfaces;

public interface SchedulerServiceInterface {
    long countStudentBySubject(int semesterId, String subjectCode);

    long countSlotBySubject(int semesterId, String subjectCode);
}
