package EIAMS.repositories;

import EIAMS.entities.ExamCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamCodeRepository extends JpaRepository<ExamCode, Integer> {
}