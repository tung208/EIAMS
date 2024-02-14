package EIAMS.repositories;

import EIAMS.entities.StudentSubject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentSubjectRepository extends JpaRepository<StudentSubject, Integer> {
}