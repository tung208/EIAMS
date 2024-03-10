package EIAMS.repositories;

import EIAMS.entities.PlanExam;
import EIAMS.entities.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlanExamRepository extends JpaRepository<PlanExam, Integer> {
    void deleteBySemesterId(int suid);
    List<PlanExam> findAllBySemesterId(String semesterId);
}