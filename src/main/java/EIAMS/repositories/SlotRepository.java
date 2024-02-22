package EIAMS.repositories;

import EIAMS.entities.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface SlotRepository extends JpaRepository<Slot, Integer> {
    @Query("SELECT s FROM Slot s WHERE s.startTime > :startTime AND s.endTime < :endTime")
    List<Slot> findSlotsByTimeRange(Date startTime, Date endTime);
}