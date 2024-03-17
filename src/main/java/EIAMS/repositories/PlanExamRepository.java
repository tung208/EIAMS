package EIAMS.repositories;

import EIAMS.entities.PlanExam;
import EIAMS.entities.Semester;
import EIAMS.entities.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface PlanExamRepository extends JpaRepository<PlanExam, Integer> {
    void deleteBySemesterId(int suid);
    List<PlanExam> findAllBySemesterId(Integer semesterId);
    List<PlanExam> findAllBySemesterIdAndSubjectCode(Integer semesterId, String subjectCode);
    List<PlanExam> findAllBySemesterIdAndSubjectCodeIn(Integer semesterId, Collection<String> subjectCode);

    @Query("SELECT s FROM PlanExam s " +
            "WHERE (s.semesterId = :semesterId)" +
            "AND (:subjectCode = '' or s.subjectCode LIKE %:subjectCode% )" )
    Page<Semester> findByDynamic(Integer semesterId, String subjectCode, Pageable pageable);
}