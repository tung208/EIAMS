package EIAMS.services;

import EIAMS.dtos.*;
import EIAMS.entities.Account;
import EIAMS.entities.Token;
import EIAMS.entities.responeObject.ResponseObject;
import EIAMS.exception.EntityNotFoundException;
import EIAMS.repositories.AccountRepository;
import EIAMS.repositories.TokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AccountRepository accountRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public ResponseObject register(RegisterRequest request) {

        var user = Account.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .active(request.getActive())
                .build();
        var savedUser = accountRepository.save(user);
        return ResponseObject.builder()
                .status("OK")
                .message("Register successfully!")
                .data("")
                .build();
    }

    public ResponseObject authenticate(AuthenticationRequest request) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        try {
            Authentication authenticatedUser = authenticationManager.authenticate(authentication);
        } catch (AuthenticationException e){
            return ResponseObject.builder()
                    .status("Wrong email or password")
                    .message("Login fail!")
                    .build();
        }

        var user = accountRepository.findByEmail(request.getEmail()).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return ResponseObject.builder()
                .status("Sucess")
                .message("Login Successfully")
                .data(AuthenticationResponse.builder()
                        .accessToken(jwtToken)
                        .refreshToken(refreshToken)
                        .build())
                .build();
    }

    private void saveUserToken(Account account, String jwtToken) {
        var token = Token.builder()
                .accountId(account.getId())
                .token(jwtToken)
                .tokenType("BREAR")
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(Account account) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(account.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            var user = this.accountRepository.findByEmail(userEmail)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    public void changePass(ChangePassDto changePassDto) throws EntityNotFoundException {
        // Lấy thông tin xác thực hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) {
            // Lấy thông tin người dùng từ UserDetails
            Account accountDetails = (Account) authentication.getPrincipal();
            // Lấy username của người dùng
            String username = accountDetails.getUsername();

                Optional<Account> accountOptional = accountRepository.findByEmail(username);
                if(accountOptional.isPresent()){
                    // Kiểm tra xem old pass có trùng với pass hiện tại không
                    if (passwordEncoder.matches(changePassDto.getOldPassword(), accountOptional.get().getPassword())) {
                        Account account = accountOptional.get();
                        account.setPassword(passwordEncoder.encode(changePassDto.getNewPassword()));
                        accountRepository.save(account);
                    } else throw new EntityNotFoundException("OldPassword incorect");
                } else throw new EntityNotFoundException("OldPassword incorect in account");
        } else throw new EntityNotFoundException("Not logged");
    }
}
