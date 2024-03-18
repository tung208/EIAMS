package EIAMS.repositories;

import EIAMS.entities.Semester;
import EIAMS.entities.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    void deleteBySemesterId(int suid);

    @Query("SELECT s FROM Subject s WHERE s.subjectCode = :subjectCode AND s.semesterId = :semesterId")
    List<Subject> findBySubjectCodeAndSemeterId(String subjectCode, int semesterId);

    Subject findBySemesterIdAndSubjectCode(Integer semesterId, String subjectCode);

    @Query("SELECT s FROM Subject s " +
            "WHERE (s.semesterId = :semesterId)" +
            "AND (:name = '' or s.subjectName LIKE %:name%)" +
            "AND (:code = '' or s.subjectCode LIKE %:code%) " )
    Page<Subject> findByDynamic(Integer semesterId, String name, String code, Pageable pageable);
}