package EIAMS.repositories;

import EIAMS.entities.Scheduler;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SchedulerRepository extends JpaRepository<Scheduler, Integer> {
    List<Scheduler> findAllBySemesterId(Integer semesterId);
    Scheduler findBySemesterIdAndRoomIdAndStartDateAndEndDate(Integer semesterId, Integer roomId, LocalDateTime startDate, LocalDateTime endDate);
}