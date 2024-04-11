package EIAMS.repositories;

import EIAMS.entities.ExamCode;
import EIAMS.entities.Lecturer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface LecturerRepository extends JpaRepository<Lecturer,Integer> {
    List<Lecturer> findAllBySemesterId(Integer semesterId);
    @Transactional
    @Modifying
    @Query("DELETE FROM Lecturer le WHERE le.semesterId = :semesterId")
    int deleteBySemesterId(Integer semesterId);

    @Query("SELECT s FROM Lecturer s " +
            "WHERE (s.semesterId = :semesterId)" +
            "AND (:email = '' or s.email LIKE %:email% )" +
            "AND (:examSubject = '' or s.examSubject LIKE %:examSubject% )"+
            "AND (:totalSlot = 0 or s.totalSlot = :totalSlot )"
    )
    Page<Lecturer> findByDynamic(Integer semesterId, String email,String examSubject, Integer totalSlot, Pageable pageable);

    Lecturer findLecturerByIdAndSemesterId(Integer id, Integer semesterId);

    List<Lecturer> findBySemesterIdAndEmail(Integer semesterId, String email);

    @Query("SELECT l FROM Lecturer l WHERE l.id NOT IN (SELECT s.lecturerId FROM Scheduler s WHERE s.semesterId = ?1 AND s.lecturerId is not null GROUP BY s.lecturerId HAVING COUNT(s.lecturerId) >= l.totalSlot)")
    List<Lecturer> findLecturersWithAvailableSlots(Integer semesterId);

    @Query("SELECT l FROM Lecturer l WHERE l.examSubject like concat('%', ?2, '%') and l.id NOT IN (SELECT s.lecturerId FROM Scheduler s WHERE s.semesterId = ?1 AND s.lecturerId is not null GROUP BY s.lecturerId HAVING COUNT(s.lecturerId) >= l.totalSlot)")
    List<Lecturer> findLecturersWithAvailableSlotsAndExamSubjectContains(Integer semesterId, String examSubject);

    int countAllBySemesterId(Integer semesterId);
}

