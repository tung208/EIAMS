package EIAMS.services;

import EIAMS.entities.Account;
import EIAMS.helper.Pagination;
import EIAMS.repositories.AccountRepository;
import EIAMS.services.interfaces.AccountServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService implements AccountServiceInterface {
    private final AccountRepository accountRepository;
    private final Pagination pagination;

    @Override
    public Page<Account> list(Integer page, Integer limit) {
        Pageable pageable = pagination.getPageable(page, limit);
        return accountRepository.findAll(pageable);
    }

    @Override
    public List<Account> list() {
        return accountRepository.findAll();
    }
}
