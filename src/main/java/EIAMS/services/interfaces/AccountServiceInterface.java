package EIAMS.services.interfaces;

import EIAMS.entities.Account;
import EIAMS.entities.Role;
import EIAMS.entities.Student;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface AccountServiceInterface {
    Page<Account> list(Integer page, Integer limit);

    List<Account> list();

    Optional<Account> getAccountDetail(int id);

    void create(Account account);

    void update(int id,Account account);

    void delete(int id);

    Page<Account> search(Integer page, Integer limit, int active, String email, Role role, String username);
}
