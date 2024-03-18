package EIAMS.repositories;

import EIAMS.entities.Scheduler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface SchedulerRepository extends JpaRepository<Scheduler, Integer> {
    List<Scheduler> findAllBySemesterId(Integer semesterId);
    List<Scheduler> findAllBySemesterIdAndSubjectCode(Integer semesterId, String subjectCode);
    Scheduler findBySemesterIdAndRoomIdAndStartDateAndEndDate(Integer semesterId, Integer roomId, LocalDateTime startDate, LocalDateTime endDate);
    List<Scheduler> findAllBySemesterIdAndEndDateAfter(Integer semesterId, LocalDateTime endDate);
    void deleteBySemesterId(Integer semesterId);
    @Query("""
            select distinct s.subjectCode, s.startDate, s.endDate from Scheduler s
            where s.semesterId = ?1 and s.startDate > ?2 and s.endDate < ?3 and s.subjectCode like concat('%', ?4, '%')""")
    List<Object> findAllBySemesterIdAndStartDateAfterAndEndDateBeforeAndSubjectCodeContains(
            Integer semesterId, LocalDateTime startDate, LocalDateTime endDate, String subjectCode);
    @Query("select distinct s.subjectCode, s.startDate, s.endDate from Scheduler s where s.semesterId = ?1 and s.subjectCode like concat('%', ?2, '%')")
    List<Object> findAllBySemesterIdAndSubjectCodeContains(Integer semesterId, String subjectCode);
    @Query("""
            select distinct s.subjectCode, s.startDate, s.endDate from Scheduler s
            where s.semesterId = ?1 and s.startDate > ?2 and s.subjectCode like concat('%', ?3, '%')""")
    List<Object> findAllBySemesterIdAndStartDateAfterAndSubjectCodeContains(
            Integer semesterId, LocalDateTime startDate, String subjectCode);
    @Query("""
            select distinct s.subjectCode, s.startDate, s.endDate from Scheduler s
            where s.semesterId = ?1 and s.endDate < ?2 and s.subjectCode like concat('%', ?3, '%')""")
    List<Object> findAllBySemesterIdAndEndDateBeforeAndSubjectCodeContains(
            Integer semesterId, LocalDateTime endDate, String subjectCode);
}