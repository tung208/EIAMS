package EIAMS.repositories;

import EIAMS.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Integer> {
//    @Transactional
//    @Modifying
//    @Query(value = "DELETE FROM Student s WHERE s.rollNumber IN ?1", nativeQuery = true)
//    void deleteByRollNumbers(List<String> rollNumbers);
    void deleteByRollNumberIn(List<String> rollNumbers);
    List<Student> findAll();
}