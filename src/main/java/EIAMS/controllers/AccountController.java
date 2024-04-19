package EIAMS.controllers;

import EIAMS.dtos.AccountDto;
import EIAMS.dtos.ChangePassDto;
import EIAMS.entities.Account;
import EIAMS.entities.Role;
import EIAMS.entities.responeObject.PageResponse;
import EIAMS.entities.responeObject.ResponseObject;
import EIAMS.exception.EntityNotFoundException;
import EIAMS.services.AccountService;
import EIAMS.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {
    @Autowired
    AccountService accountService;

    @Autowired
    AuthenticationService authenticationService;

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

    @PutMapping()
    public ResponseEntity<ResponseObject> updateAccount(@RequestBody AccountDto accountDto) throws EntityNotFoundException {
        return ResponseEntity.ok(accountService.updateUser(accountDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> updateAccount(@PathVariable int id) throws EntityNotFoundException {
        return ResponseEntity.ok(accountService.delete(id));
    }

    @PostMapping("/change-pass")
    public ResponseEntity<ResponseObject> changePass(
            @RequestBody ChangePassDto request
    ) throws IOException, EntityNotFoundException {
        authenticationService.changePass(request);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Change Pass Success!", ""));
    }

}
