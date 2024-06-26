package EIAMS.repositories;

import EIAMS.entities.ExamCode;
import EIAMS.entities.Room;
import EIAMS.entities.Semester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer> {
    void deleteBySemesterId(int suid);
    List<Room> findAllByType(String type);
    int countAllBySemesterId(Integer semesterId);

    List<Room> findAllBySemesterIdAndQuantityStudentGreaterThanAndNameContainingIgnoreCase(Integer semesterId, Integer quantityStudent, String name);
    List<Room> findAllBySemesterIdAndQuantityStudentGreaterThanAndNameNotContainingIgnoreCase(Integer semesterId, Integer quantityStudent, String name);
    @Query("SELECT s FROM Room s " +
            "WHERE (s.semesterId = :semesterId)" +
            "AND (:name = '' or s.name LIKE %:name% )" )
    Page<Room> findByDynamic(Integer semesterId, String name, Pageable pageable);

    @Query("select r from Room r where r.id in ?1 and upper(r.name) like upper(concat('%', ?2, '%'))")
    List<Room> findAllByIdInAndNameContainsIgnoreCase(Collection<Integer> id, String name);

    // Xóa các bản ghi dựa trên semesterId
    void deleteBySemesterId(Integer semesterId);

    @Query("select r.id from Room r where r.semesterId = ?1 and r.id not in ?2")
    List<Integer> findAllBySemesterIdAndIdNotIn(Integer semesterId, Collection<Integer> id);
}