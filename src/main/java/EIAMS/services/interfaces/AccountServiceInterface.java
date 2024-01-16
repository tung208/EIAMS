package EIAMS.services.interfaces;

import EIAMS.entities.Account;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AccountServiceInterface {
    Page<Account> list(Integer page, Integer limit);
    List<Account> list();
}