package EIAMS.repositories;

import EIAMS.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> getAccountByEmail(String email);
}