package EIAMS.repositories;

import EIAMS.entities.Room;
import EIAMS.entities.Student;
import EIAMS.entities.StudentSubject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface StudentSubjectRepository extends JpaRepository<StudentSubject, Integer> {
    void deleteBySemesterId(int suid);
    @Query("SELECT s FROM StudentSubject s WHERE s.rollNumber = :rollNumber AND s.groupName = :groupName AND s.semesterId = :semesterId")
    List<StudentSubject> findByRollNumberAndGroupNameAndSemesterId(String rollNumber, String groupName, int semesterId);
    List<StudentSubject> findAllBySemesterIdAndSubjectCodeAndBlackList(Integer semesterId, String subjectCode,Integer blackList);
    List<StudentSubject> findAllBySemesterIdAndSubjectCode(Integer semesterId, String subjectCode);
    List<StudentSubject> findAllBySemesterIdAndSubjectCodeIn(Integer semesterId, Collection<String> subjectCode);
    List<StudentSubject> findAllBySemesterIdAndIdIn(Integer semesterId, Collection<Integer> id);
    List<StudentSubject> findAllBySemesterIdAndBlackListAndSubjectCodeIn(Integer semesterId, Integer blackList, Collection<String> subjectCode);

    @Query("SELECT s FROM StudentSubject s "
            + "WHERE (s.semesterId = :semesterId)"
            + "AND (:rollNumber = '' or s.rollNumber LIKE %:rollNumber% )"
            + "AND (:subjectCode = '' or s.subjectCode LIKE %:subjectCode% )"
            + "AND (:groupName = '' or s.groupName LIKE %:groupName% )"
            + "AND (:blackList = 10 or s.blackList = :blackList )"
    )
    Page<StudentSubject> findByDynamic(int semesterId, String rollNumber, String subjectCode, String groupName, int blackList, Pageable pageable);
    StudentSubject findBySemesterIdAndSubjectCodeAndRollNumber(Integer semesterId, String subjectCode, String rollNumber);
}