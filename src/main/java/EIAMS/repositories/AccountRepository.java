package EIAMS.repositories;

import EIAMS.entities.Account;
import EIAMS.entities.Role;
import EIAMS.entities.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> getAccountByEmail(String email);

    Optional<Account> findById(int id);

    List<Account> findAll();
    Optional<Account> findByEmail(String email);

    @Query("SELECT s FROM Account s " +
            "WHERE (:active = 10 or s.active = :active )" +
            "AND (:email = '' or s.email LIKE %:email% ) " +
            "AND (:role IS NULL or s.role = :role ) " +
            "AND (:username = '' or s.username LIKE %:username% ) "
    )
    Page<Account> findByDynamic(int active, String email,Role role,String username, Pageable pageable);
}