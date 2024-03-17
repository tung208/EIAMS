package EIAMS.repositories;

import EIAMS.entities.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Student s WHERE s.rollNumber IN :rollNumbers")
    void deleteByRollNumbers(List<String> rollNumbers);
//    void deleteByRollNumberIn(List<String> rollNumbers);

    Optional<Student> findByRollNumber(String rollNumber);

    Page<Student> findAllByRollNumberContainingIgnoreCaseOrFullNameContainingIgnoreCaseAndMemberCodeContainingIgnoreCase(
            String rollNumber, String fullName, String memberCode, Pageable pageable);

    Page<Student> findAllByRollNumberInAndFullNameContainingIgnoreCaseOrCmtndContainingIgnoreCaseAndMemberCodeContainingIgnoreCase(
            Collection<String> rollNumbers, String fullName, String cmt, String memberCode, Pageable pageable);
}