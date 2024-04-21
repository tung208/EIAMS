package EIAMS.repositories;

import EIAMS.entities.Account;
import EIAMS.entities.ActionLog;
import EIAMS.entities.Role;
import EIAMS.entities.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ActionLogRepository extends JpaRepository<ActionLog, Integer> {
    @Query("SELECT s FROM ActionLog s " +
            "WHERE (s.semesterId = :semesterId)" +
            "AND (:userName = '' or s.userName LIKE %:userName% )" )
    Page<ActionLog> findByDynamic(Integer semesterId, String userName, Pageable pageable);
}
