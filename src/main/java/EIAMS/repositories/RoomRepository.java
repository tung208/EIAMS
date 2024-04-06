package EIAMS.repositories;

import EIAMS.entities.ExamCode;
import EIAMS.entities.Room;
import EIAMS.entities.Semester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer> {
    void deleteBySemesterId(int suid);
    List<Room> findAllByType(String type);

    List<Room> findAllBySemesterIdAndQuantityStudentGreaterThanAndNameContainingIgnoreCase(Integer semesterId, Integer quantityStudent, String name);
    List<Room> findAllBySemesterIdAndQuantityStudentGreaterThanAndNameNotContainingIgnoreCase(Integer semesterId, Integer quantityStudent, String name);
    @Query("SELECT s FROM Room s " +
            "WHERE (s.semesterId = :semesterId)" +
            "AND (:name = '' or s.name LIKE %:name% )" )
    Page<Room> findByDynamic(Integer semesterId, String name, Pageable pageable);

    List<Room> findAllByIdIn(List<Integer> ids);

}