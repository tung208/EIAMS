package EIAMS.repositories;

import EIAMS.entities.Scheduler;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchedulerRepository extends JpaRepository<Scheduler, Integer> {
}