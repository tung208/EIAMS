package EIAMS.repositories;

import EIAMS.entities.ExamCode;
import EIAMS.entities.Semester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ExamCodeRepository extends JpaRepository<ExamCode, Integer> {

    @Transactional
    @Modifying
    @Query("DELETE FROM ExamCode ex WHERE ex.semesterId = :semesterId")
    int deleteBySemesterId(int semesterId);

    ExamCode findBySemesterIdAndSlotIdAndSubjectCode(Integer semesterId, Integer slotId, String subjectCode);

    @Query("SELECT s FROM ExamCode s " +
            "WHERE (s.semesterId = :semesterId)" +
            "AND (:subjectCode = '' or s.code LIKE %:subjectCode% )" )
    Page<Semester> findByDynamic(Integer semesterId,String subjectCode, Pageable pageable);
}