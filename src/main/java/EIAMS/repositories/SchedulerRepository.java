package EIAMS.repositories;

import EIAMS.entities.Scheduler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SchedulerRepository extends JpaRepository<Scheduler, Integer> {
    List<Scheduler> findAllBySemesterId(Integer semesterId);
    List<Scheduler> findAllBySemesterIdAndSubjectCode(Integer semesterId, String subjectCode);
    Scheduler findBySemesterIdAndRoomIdAndStartDateAndEndDate(Integer semesterId, Integer roomId, LocalDateTime startDate, LocalDateTime endDate);
    List<Scheduler> findAllBySemesterIdAndStartDateBeforeOrEndDateAfter(Integer semesterId, LocalDateTime startDate, LocalDateTime endDate);
    void deleteBySemesterId(Integer semesterId);
    Page<Scheduler> findAllBySemesterIdAndStartDateAfterAndEndDateBeforeAndSubjectCodeContains(
            Integer semesterId, LocalDateTime startDate, LocalDateTime endDate, String subjectCode, Pageable pageable);
    Page<Scheduler> findAllBySemesterIdAndSubjectCodeContains(Integer semesterId, String subjectCode, Pageable pageable);
    Page<Scheduler> findAllBySemesterIdAndStartDateAfterAndSubjectCodeContains(
            Integer semesterId, LocalDateTime startDate, String subjectCode, Pageable pageable);
    Page<Scheduler> findAllBySemesterIdAndEndDateBeforeAndSubjectCodeContains(
            Integer semesterId, LocalDateTime endDate, String subjectCode, Pageable pageable);
}