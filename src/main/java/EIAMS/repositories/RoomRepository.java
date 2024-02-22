package EIAMS.repositories;

import EIAMS.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Integer> {
    List<Room> findAllByType(String type);
    void deleteBySemesterId(int suid);
}