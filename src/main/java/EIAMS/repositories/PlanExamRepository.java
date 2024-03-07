package EIAMS.repositories;

import EIAMS.entities.PlanExam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanExamRepository extends JpaRepository<PlanExam, Integer> {
    List<PlanExam> findAllBySemesterId(String semesterId);
}