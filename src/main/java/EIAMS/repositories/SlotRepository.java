package EIAMS.repositories;

import EIAMS.entities.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface SlotRepository extends JpaRepository<Slot, Integer> {
    @Query("SELECT s FROM Slot s " +
            "WHERE (HOUR(s.startTime)*60 + MINUTE(s.startTime)) > (HOUR(:startTime)*60 + MINUTE(:startTime)) " +
            "AND (HOUR(s.endTime)*60 + MINUTE(s.endTime)) < (HOUR(:endTime)*60 + MINUTE(:endTime))")
    Slot findSlotByTimeRange(LocalDateTime startTime, LocalDateTime endTime);
}