package EIAMS.repositories;

import EIAMS.entities.ExamCode;
import EIAMS.entities.Lecturer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface LecturerRepository extends JpaRepository<Lecturer,Integer> {
    @Transactional
    @Modifying
    @Query("DELETE FROM Lecturer le WHERE le.semesterId = :semesterId")
    int deleteBySemesterId(Integer semesterId);

    @Query("SELECT s FROM Lecturer s " +
            "WHERE (s.semesterId = :semesterId)" +
            "AND (:email = '' or s.email LIKE %:email% )" +
            "AND (:examSubject = '' or s.examSubject LIKE %:examSubject% )"+
            "AND (:totalSlot = 0 or s.totalSlot = :totalSlot )"
    )
    Page<Lecturer> findByDynamic(Integer semesterId, String email,String examSubject, Integer totalSlot, Pageable pageable);
}

