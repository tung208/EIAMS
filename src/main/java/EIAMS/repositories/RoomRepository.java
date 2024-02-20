package EIAMS.repositories;

import EIAMS.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Integer> {
    void deleteBySemesterId(int suid);
}