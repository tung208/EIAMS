package EIAMS.repositories;

import EIAMS.entities.Student;
import EIAMS.entities.StudentSubject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface StudentSubjectRepository extends JpaRepository<StudentSubject, Integer> {
    void deleteBySemesterId(int suid);
    @Query("SELECT s FROM StudentSubject s WHERE s.rollNumber = :rollNumber AND s.groupName = :groupName AND s.semesterId = :semesterId")
    List<StudentSubject> findByRollNumberAndGroupNameAndSemesterId(String rollNumber, String groupName, int semesterId);
    List<StudentSubject> findAllBySemesterIdAndSubjectCodeAndBlackList(Integer semesterId, String subjectCode,Integer blackList);
    List<StudentSubject> findAllBySemesterIdAndSubjectCode(Integer semesterId, String subjectCode);
    List<StudentSubject> findAllBySemesterIdAndSubjectCodeIn(Integer semesterId, Collection<String> subjectCode);
    List<StudentSubject> findAllBySemesterIdAndIdIn(Integer semesterId, Collection<Integer> id);
    List<StudentSubject> findAllBySemesterIdAndBlackListAndSubjectCodeIn(Integer semesterId, Integer blackList, Collection<String> subjectCode);
}