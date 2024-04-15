package EIAMS.repositories;

import EIAMS.entities.PlanExam;
import EIAMS.entities.Semester;
import EIAMS.entities.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public interface PlanExamRepository extends JpaRepository<PlanExam, Integer> {
    void deleteBySemesterId(int suid);
    List<PlanExam> findAllBySemesterId(Integer semesterId);
    List<PlanExam> findAllBySemesterIdAndSubjectCode(Integer semesterId, String subjectCode);
    List<PlanExam> findAllBySemesterIdAndExpectedDateAndExpectedTimeAndSubjectCodeIn(Integer semesterId, Date expectedDate, String expectedTime, Collection<String> subjectCode);
    List<PlanExam> findAllBySemesterIdAndExpectedDateAndExpectedTime(Integer semesterId, Date expectedDate, String expectedTime);
    List<PlanExam> findAllBySemesterIdAndSubjectCodeIn(Integer semesterId, Collection<String> subjectCode);
    PlanExam findBySemesterIdAndSubjectCodeAndExpectedDateAndExpectedTime(Integer semesterId, String subjectCode, Date expectedDate, String expectedTime);

    @Query("SELECT s FROM PlanExam s " +
            "WHERE (s.semesterId = :semesterId)" +
            "AND (:subjectCode = '' or s.subjectCode LIKE %:subjectCode% )" )
    Page<PlanExam> findByDynamic(Integer semesterId, String subjectCode, Pageable pageable);

    @Query("SELECT s FROM PlanExam s "
            + "WHERE (s.semesterId = :semesterId)"
            + "AND DATEDIFF(s.expectedDate, :expectedDate) = 0"
            + "AND UPPER(s.expectedTime) = UPPER(:expectedTime)"
            + "AND UPPER(s.typeExam) = UPPER(:typeExam)"
            + "AND UPPER(s.subjectCode) = UPPER(:subjectCode)")
    List<PlanExam> findBySameObject(Integer semesterId, java.sql.Date expectedDate, String expectedTime,String typeExam, String subjectCode);

    // Xóa các bản ghi dựa trên semesterId
    void deleteBySemesterId(Integer semesterId);
}