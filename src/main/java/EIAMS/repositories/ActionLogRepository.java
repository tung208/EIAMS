package EIAMS.repositories;

import EIAMS.entities.Account;
import EIAMS.entities.ActionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionLogRepository extends JpaRepository<ActionLog, Integer> {
}
