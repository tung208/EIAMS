package EIAMS.controllers;

import EIAMS.entities.Account;
import EIAMS.entities.Role;
import EIAMS.entities.Student;
import EIAMS.entities.responeObject.PageResponse;
import EIAMS.repositories.AccountRepository;
import EIAMS.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {
    @Autowired
    AccountService accountService;

    @GetMapping()
    public PageResponse<Account> list(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "2") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "10") int active,
            @RequestParam(defaultValue = "") String email,
            @RequestParam(defaultValue = "") String role,
            @RequestParam(defaultValue = "") String username
    ) {
        Role roleEnum = Role.fromString(role.toUpperCase());
        Page<Account> page = accountService.search(pageNo, pageSize, active, email, roleEnum, username );
        return new PageResponse<>(page.getNumber() + 1, page.getTotalPages(), page.getSize(), page.getContent());
    }
}
