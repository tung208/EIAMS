package EIAMS.repositories;

import EIAMS.entities.Scheduler;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface SchedulerRepository extends JpaRepository<Scheduler, Integer> {
    List<Scheduler> findAllBySemesterId(Integer semesterId);
    List<Scheduler> findAllBySemesterIdOrderByStartDate(Integer semesterId);
    List<Scheduler> findAllBySemesterIdAndSubjectCodeContainingOrderByStartDate(Integer semesterId, String subjectCode);
    Scheduler findBySemesterIdAndRoomIdAndStartDateAndEndDate(Integer semesterId, Integer roomId, LocalDateTime startDate, LocalDateTime endDate);
    List<Scheduler> findAllBySemesterIdAndStartDateAndEndDateAndTypeAndRoomIdNotIn(Integer semesterId, LocalDateTime startDate, LocalDateTime endDate, String type, Collection<Integer> roomId);
    List<Scheduler> findAllBySemesterIdAndStartDateAndEndDateAndTypeAndRoomIdIn(Integer semesterId, LocalDateTime startDate, LocalDateTime endDate, String type, Collection<Integer> roomId);
    List<Scheduler> findAllBySemesterIdAndStartDateAndEndDateAndIdNotIn(Integer semesterId, LocalDateTime startDate, LocalDateTime endDate, Collection<Integer> id);
    @Query("select s.roomId from Scheduler s where s.semesterId = ?1 and ((s.startDate >= ?2 and s.endDate <= ?3) or (s.startDate < ?2 and s.endDate > ?2) or (s.startDate < ?3 and s.endDate > ?3))")
    List<Integer> findAllRoomIdBySemesterIdAndStartDateAndEndDate(Integer semesterId, LocalDateTime startDate, LocalDateTime endDate);
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

    @Query("select s from Scheduler s where s.semesterId = ?1 and s.id <> ?4 and s.lecturerId = ?5 " +
            "and ((s.startDate >= ?2 and s.endDate <= ?3) or (s.startDate < ?2 and s.endDate > ?2) or (s.startDate < ?3 and s.endDate > ?3)) order by s.startDate asc")
    List<Scheduler> findAllBySemesterIdAndStartDateAndEndDateAndLectureId(Integer semesterId, LocalDateTime startDate, LocalDateTime endDate, Integer id, Integer lectureId);

    void deleteBySemesterId(Integer semesterId);

    @Query("""
            select distinct s.roomId from Scheduler s
            where s.startDate > ?1 and s.endDate < ?2 and s.lecturerId = ?3
            order by s.roomId asc
            """)
    List<Integer> findAllRoomByStartDateAfterAndEndDateBeforeAndLecturerId(LocalDateTime startDate, LocalDateTime endDate, Integer lecturerId);

    @Query("""
            select DISTINCT s.roomId, function('date', s.startDate) from Scheduler s
            where function('date', s.startDate) >= function('date', ?1) and function('date', s.endDate) <= function('date', ?2) and s.lecturerId = ?3 and s.semesterId = ?4
            order by s.startDate asc
            """)
    List<Object[]> findAllRoomByDateAndLecturerId(LocalDateTime startDate, LocalDateTime endDate, Integer lecturerId, Integer semesterId);


    @Query("""
            select distinct s.roomId from Scheduler s
            where s.startDate > ?1 and s.endDate < ?2
            order by s.roomId asc
            """)
    List<Integer> findAllRoomByStartDateAfterAndEndDateBefore(LocalDateTime startDate, LocalDateTime endDated);

    @Query("""
             select DISTINCT s.roomId, function('date', s.startDate) from Scheduler s
            where function('date', s.startDate) >= function('date', ?1) and function('date', s.endDate) <= function('date', ?2) and s.semesterId = ?3
            order by s.startDate asc
            """)
    List<Object[]> findAllRoomByDate(LocalDateTime startDate, LocalDateTime endDate, Integer semesterId);
    @Query("""
             select DISTINCT s.roomId, function('date', s.startDate) from Scheduler s
            where s.lecturerId is null and function('date', s.startDate) >= function('date', ?1) and function('date', s.endDate) <= function('date', ?2) and s.semesterId = ?3
            """)
    List<Object[]> findAllRoomByDateAndLecturerIdIsNull(LocalDateTime startDate, LocalDateTime endDate, Integer semesterId);

    @Query("select distinct s.roomId from Scheduler s where s.lecturerId = ?1" +
            "order by s.roomId asc")
    List<Integer> findAllRoomByLecturerId(Integer lecturerId);

    @Query("select distinct s.roomId from Scheduler s " +
            "order by s.roomId asc")
    List<Integer> findAllRoom();

    @Query("""
            select distinct s.roomId from Scheduler s
            where s.startDate > ?1 and s.lecturerId = ?2
            order by s.roomId asc
            """)
    List<Integer> findAllRoomByStartDateAfterAndLecturerId(LocalDateTime startDate, Integer lecturerId);

    @Query("""
            select distinct s.roomId from Scheduler s
            where s.startDate > ?1
            order by s.roomId asc
            """)
    List<Integer> findAllRoomByStartDateAfter(LocalDateTime startDate);

    @Query("""
            select distinct s.roomId from Scheduler s
            where s.endDate < ?1 and s.lecturerId = ?2
            order by s.roomId asc
            """)
    List<Integer> findAllRoomByEndDateBeforeAndLecturerId(LocalDateTime endDate, Integer lecturerId);

    @Query("""
            select distinct s.roomId from Scheduler s
            where s.endDate < ?1
            order by s.roomId asc
            """)
    List<Integer> findAllRoomByEndDateBefore(LocalDateTime endDate);

    @Query("""
            select distinct s.subjectCode, s.startDate, s.endDate from Scheduler s
            where s.startDate > ?1 and s.endDate < ?2 and s.subjectCode like concat('%', ?3, '%')
            order by s.startDate asc
            """)
    List<Object> findAllByStartDateAfterAndEndDateBeforeAndSubjectCodeContains(LocalDateTime startDate, LocalDateTime endDate, String subjectCode);
    @Query("select distinct s.subjectCode, s.startDate, s.endDate from Scheduler s where s.subjectCode like concat('%', ?1, '%')" +
            "order by s.startDate asc")
    List<Object> findAllBySubjectCodeContains(String subjectCode);
    @Query("""
            select distinct s.subjectCode, s.startDate, s.endDate from Scheduler s
            where s.startDate > ?1 and s.subjectCode like concat('%', ?2, '%')
            order by s.startDate asc
            """)
    List<Object> findAllByStartDateAfterAndSubjectCodeContains(LocalDateTime startDate, String subjectCode);
    @Query("""
            select distinct s.subjectCode, s.startDate, s.endDate from Scheduler s
            where s.endDate < ?1 and s.subjectCode like concat('%', ?2, '%')
            order by s.startDate asc
            """)
    List<Object> findAllByEndDateBeforeAndSubjectCodeContains(LocalDateTime endDate, String subjectCode);

    long countAllBySemesterId(Integer semesterId);

    @Query("select count(s) from Scheduler s where s.semesterId = ?1 and s.lecturerId = ?2")
    int countAllBySemesterIdAndLecturerId(Integer semesterId, Integer lecturerId);

    List<Scheduler> findAllBySemesterIdAndSubjectCodeIn(Integer semesterId, Collection<String> subjectCode);

    List<Scheduler> findAllBySemesterIdAndSubjectCodeNotIn(Integer semesterId, Collection<String> subjectCode);

    @Query("""
            select s from Scheduler s
            where s.semesterId = ?1 and s.lecturerId = ?2 and ((s.startDate >= ?3 and s.endDate <= ?4) or (s.startDate <= ?3 and s.endDate >= ?3) or (s.startDate <= ?4 and s.endDate >= ?4))""")
    List<Scheduler> findBySemesterIdAndLecturerIdAvailable(Integer semesterId, Integer lecturerId, LocalDateTime startDate, LocalDateTime endDate);

    @Modifying
    @Query("UPDATE Scheduler s SET s.lecturerId = null WHERE s.semesterId = ?1 AND s.lecturerId IS NOT NULL")
    @Transactional
    void resetLecturerId(int semesterId);

    @Query("select s from Scheduler s where s.roomId = ?1 and s.startDate >= ?2 and s.endDate <= ?3 and s.semesterId = ?4")
    List<Scheduler> findAllByRoomIdAndStartDateAfterAndEndDateBefore(Integer roomId, LocalDateTime startDate, LocalDateTime endDate, Integer semesterId);
    List<Scheduler> findAllBySemesterIdAndRoomIdAndStartDateAfterAndEndDateBeforeAndLecturerId(Integer semesterId, Integer roomId, LocalDateTime startDate, LocalDateTime endDate, Integer lecturerId);

    @Query("select s.id from Scheduler s where s.semesterId = ?1 and s.startDate >= ?2 and s.endDate <= ?3")
    List<Integer> findAllIdBySemesterIdAndStartDateAfterAndEndDateBefore(Integer semesterId, LocalDateTime startDate, LocalDateTime endDate);


    @Query("select s from Scheduler s where s.semesterId = ?1 and s.startDate > ?2 and s.endDate < ?3 and s.id <> ?4 and s.lecturerId <> ?5")
    List<Scheduler> findAllBySemesterIdAndStartDateAfterAndEndDateBeforeAndIdNot(Integer semesterId, LocalDateTime startDate, LocalDateTime endDate, Integer id, Integer lecturerId);
}