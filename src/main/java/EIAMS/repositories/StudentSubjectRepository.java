package EIAMS.repositories;

import EIAMS.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentSubjectRepository extends JpaRepository<Student, Integer> {
}