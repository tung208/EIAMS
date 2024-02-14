package EIAMS.repositories;

import EIAMS.entities.Student;
import EIAMS.entities.StudentSubject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentSubjectRepository extends JpaRepository<StudentSubject, Integer> {
    long countAllBySemesterIdAndSubjectCode(int semesterId, String subjectCode);
    long countAllBySemesterId(int semesterId);

}