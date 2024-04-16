package EIAMS.services;

import EIAMS.dtos.AccountDto;
import EIAMS.entities.Account;
import EIAMS.entities.Role;
import EIAMS.entities.responeObject.ResponseObject;
import EIAMS.exception.EntityNotFoundException;
import EIAMS.helper.Pagination;
import EIAMS.repositories.AccountRepository;
import EIAMS.services.interfaces.AccountServiceInterface;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AccountService implements AccountServiceInterface {
    private final AccountRepository accountRepository;
    private final Pagination pagination;
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<Account> list(Integer page, Integer limit) {
        Pageable pageable = pagination.getPageable(page, limit);
        return accountRepository.findAll(pageable);
    }

    @Override
    public List<Account> list() {
        return accountRepository.findAll();
    }

    @Override
    public Optional<Account> getAccountDetail(int id) {
        return accountRepository.findById(id);
    }

    @Override
    public void create(Account account) {
        accountRepository.save(account);
    }

    @Override
    public void update(int id, Account account) {
        Optional<Account> a = accountRepository.findById(id);
        if(a.isPresent()){
            Account accountUpdate = a.get();
            accountUpdate.setActive(account.getActive());
            accountUpdate.setEmail(account.getEmail());
            accountUpdate.setPassword(account.getPassword());
//            accountUpdate.setRole(account.getRole());
            accountUpdate.setUsername(getUserName(account.getEmail()));

            accountRepository.save(accountUpdate);
        }
    }

    @Override
    public void delete(int id) {

    }

    @Override
    public Page<Account> search(Integer page, Integer limit, int active, String email, Role role, String username) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("id").descending());
        return accountRepository.findByDynamic(active, email,role, username, pageable);
    }

    public String getUserName(String email) {
        if (email == null || email.isEmpty()) {
            return null;
        }
        int atIndex = email.indexOf('@');
        if (atIndex != -1) {
            return email.substring(0, atIndex);
        } else {
            return null;
        }
    }

    public ResponseObject updateUser(AccountDto account) throws EntityNotFoundException {
        Optional<Account> accountOptional = accountRepository.findByEmail(account.getEmail());
        if (!accountOptional.isPresent()){
            throw new EntityNotFoundException("Not found accout");
        }
        var user = Account.builder()
                .id(accountOptional.get().getId())
                .username(accountOptional.get().getUsername())
                .email(accountOptional.get().getEmail())
                .password(passwordEncoder.encode(account.getPassword()))
                .role(account.getRole())
                .active(account.getActive())
                .build();
        accountRepository.save(user);
        return ResponseObject.builder()
                .status("OK")
                .message("Update successfully!")
                .data("")
                .build();
    }
}
