package EIAMS.repositories;

import EIAMS.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> getAccountByEmail(String email);

    Optional<Account> findById(int id);

    List<Account> findAll();
    Optional<Account> findByEmail(String email);

}