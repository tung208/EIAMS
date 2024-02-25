package EIAMS.repositories;

import EIAMS.entities.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface SlotRepository extends JpaRepository<Slot, Integer> {
    @Query("SELECT s FROM Slot s WHERE s.startTime > :startTime AND s.endTime < :endTime")
    Slot findSlotByTimeRange(LocalDateTime startTime, LocalDateTime endTime);
}