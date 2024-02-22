package EIAMS.repositories;

import EIAMS.entities.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    void deleteBySemesterId(int suid);

    @Query("SELECT s FROM Subject s WHERE s.subjectCode = :subjectCode AND s.semesterId = :semesterId")
    List<Subject> findBySubjectCodeAndSemeterId(String subjectCode, int semesterId);

    List<Subject> findAllByDontMix(Integer dontMix);
    List<Subject> findAllByDontMixAndNoLab(Integer dontMix, Integer noLab);

    Subject findBySemesterIdAndSubjectCode(Integer semesterId, String subjectCode);
}