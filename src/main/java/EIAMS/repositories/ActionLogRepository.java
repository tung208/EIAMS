package EIAMS.repositories;

import EIAMS.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionLogRepository extends JpaRepository<Account, Integer> {
}
