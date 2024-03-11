package EIAMS.repositories;

import EIAMS.entities.ExamCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ExamCodeRepository extends JpaRepository<ExamCode, Integer> {

    @Transactional
    @Modifying
    @Query("DELETE FROM ExamCode ex WHERE ex.semesterId = :semesterId")
    int deleteBySemesterId(int semesterId);

    ExamCode findBySemesterIdAndSubjectCode(Integer semesterId, String subjectCode);
}