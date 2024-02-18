package EIAMS.repositories;

import EIAMS.entities.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    void deleteBySemesterId(int suid);
}