package EIAMS.repositories;

import EIAMS.entities.Scheduler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface SchedulerRepository extends JpaRepository<Scheduler, Integer> {
    List<Scheduler> findAllBySemesterId(Integer semesterId);
    List<Scheduler> findAllBySemesterIdOrderByStartDate(Integer semesterId);
    List<Scheduler> findAllBySemesterIdAndSubjectCodeContainingOrderByStartDate(Integer semesterId, String subjectCode);
    Scheduler findBySemesterIdAndRoomIdAndStartDateAndEndDate(Integer semesterId, Integer roomId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("""
            select s from Scheduler s
            where s.semesterId = ?1 and s.subjectCode <> ?2 and s.startDate = ?3 and s.endDate = ?4""")
    List<Scheduler> findAllBySemesterIdAndSubjectCodeNotAndStartDateAndEndDate(Integer semesterId, String subjectCode, LocalDateTime startDate, LocalDateTime endDate);

    @Query("""
            select s from Scheduler s
            where s.semesterId = ?1 and s.subjectCode not in ?2 and s.startDate = ?3 and s.endDate = ?4""")
    List<Scheduler> findAllBySemesterIdAndSubjectCodeNotInAndStartDateAndEndDate(Integer semesterId, Collection<String> subjectCode, LocalDateTime startDate, LocalDateTime endDate);

    @Query("select s from Scheduler s where s.semesterId = ?1 and " +
            "((s.startDate > ?2 and s.endDate <= ?3) or (s.startDate >= ?2 and s.endDate < ?3) or (s.startDate < ?2 and s.endDate > ?2) or (s.startDate < ?3 and s.endDate > ?3)) order by s.startDate asc")
    List<Scheduler> findAllBySemesterIdAndStartDateBeforeAndEndDateAfter(Integer semesterId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("select s from Scheduler s where s.semesterId = ?1 and s.id <> ?4 and s.lecturerId = ?5 and " +
            "((s.startDate > ?2 and s.endDate <= ?3) or (s.startDate >= ?2 and s.endDate < ?3) or (s.startDate < ?2 and s.endDate > ?2) or (s.startDate < ?3 and s.endDate > ?3)) order by s.startDate asc")
    List<Scheduler> findAllBySemesterIdAndStartDateAndEndDateAndIdNotAndLectureId(Integer semesterId, LocalDateTime startDate, LocalDateTime endDate, Integer id, Integer lectureId);

    @Query("select s from Scheduler s where s.semesterId = ?1 and s.lecturerId = ?4 and " +
            "((s.startDate > ?2 and s.endDate <= ?3) or (s.startDate >= ?2 and s.endDate < ?3) or (s.startDate < ?2 and s.endDate > ?2) or (s.startDate < ?3 and s.endDate > ?3)) order by s.startDate asc")
    List<Scheduler> findAllBySemesterIdAndStartDateAndEndDateAndLectureId(Integer semesterId, LocalDateTime startDate, LocalDateTime endDate, Integer id, Integer lectureId);

    void deleteBySemesterId(Integer semesterId);

    @Query("""
            select distinct s.roomId from Scheduler s
            where s.startDate > ?1 and s.endDate < ?2 and s.subjectCode like concat('%', ?3, '%')
            order by s.roomId asc
            """)
    List<Integer> findAllByStartDateAfterAndEndDateBeforeAndSubjectCodeContains(LocalDateTime startDate, LocalDateTime endDate, String subjectCode);
    @Query("select distinct s.roomId from Scheduler s where s.subjectCode like concat('%', ?1, '%')" +
            "order by s.roomId asc")
    List<Integer> findAllBySubjectCodeContains(String subjectCode);
    @Query("""
            select distinct s.roomId from Scheduler s
            where s.startDate > ?1 and s.subjectCode like concat('%', ?2, '%')
            order by s.roomId asc
            """)
    List<Integer> findAllByStartDateAfterAndSubjectCodeContains(LocalDateTime startDate, String subjectCode);
    @Query("""
            select distinct s.roomId from Scheduler s
            where s.endDate < ?1 and s.subjectCode like concat('%', ?2, '%')
            order by s.roomId asc
            """)
    List<Integer> findAllByEndDateBeforeAndSubjectCodeContains(LocalDateTime endDate, String subjectCode);

    long countAllBySemesterId(Integer semesterId);

    int countAllBySemesterIdAndLecturerId(Integer semesterId, Integer lecturerId);

    List<Scheduler> findAllBySemesterIdAndSubjectCodeIn(Integer semesterId, Collection<String> subjectCode);

    List<Scheduler> findAllBySemesterIdAndSubjectCodeNotIn(Integer semesterId, Collection<String> subjectCode);

    @Query("""
            select s from Scheduler s
            where s.semesterId = ?1 and s.lecturerId = ?2 and ((s.startDate >= ?3 and s.endDate <= ?4) or (s.startDate <= ?3 and s.endDate >= ?3) or (s.startDate <= ?4 and s.endDate >= ?4))""")
    List<Scheduler> findBySemesterIdAndLecturerIdAvailable(Integer semesterId, Integer lecturerId, LocalDateTime startDate, LocalDateTime endDate);

    @Modifying
    @Query("""
        update Scheduler s set s.lecturerId = 0 where s.semesterId = ?1""")
    void resetLecturerId(int semesterId);

    @Query("select s from Scheduler s where s.roomId = ?1 and s.startDate >= ?2 and s.endDate <= ?3")
    List<Scheduler> findAllByRoomIdAndStartDateAfterAndEndDateBefore(Integer roomId, LocalDateTime startDate, LocalDateTime endDate);
}