package EIAMS.repositories;

import EIAMS.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer> {
    void deleteBySemesterId(int suid);
    List<Room> findAllByType(String type);

    List<Room> findAllByQuantityStudentGreaterThanAndNameContainingIgnoreCase(Integer quantityStudent, String name);
    List<Room> findAllByQuantityStudentGreaterThanAndNameNotContainingIgnoreCase(Integer quantityStudent, String name);
}