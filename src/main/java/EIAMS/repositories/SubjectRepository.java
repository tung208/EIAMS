package EIAMS.repositories;

import EIAMS.entities.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    void deleteBySemesterId(int suid);
    List<Subject> findAllByDontMix(Integer dontMix);
    List<Subject> findAllByDontMixAndNoLab(Integer dontMix, Integer noLab);

    Subject findBySemesterIdAndSubjectCode(Integer semesterId, String subjectCode);
}