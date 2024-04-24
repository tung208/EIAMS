package EIAMS.controllers;

import EIAMS.dtos.AuthenticationRequest;
import EIAMS.dtos.AuthenticationResponse;
import EIAMS.dtos.ChangePassDto;
import EIAMS.dtos.RegisterRequest;
import EIAMS.entities.responeObject.ResponseObject;
import EIAMS.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    @Autowired
    private final AuthenticationService service;

    @PostMapping("/register")
    public ResponseEntity<ResponseObject> register(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }
    @PostMapping("/authenticate")
    public ResponseEntity<ResponseObject> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(service.authenticate(request));
    }

//    @PostMapping("/refresh-token")
//    public void refreshToken(
//            HttpServletRequest request,
//            HttpServletResponse response
//    ) throws IOException {
//        service.refreshToken(request, response);
//    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ResponseObject> refreshToken(
           @RequestBody String refreshToken
    ) throws IOException {
        ResponseObject responseObject =  service.refreshToken(refreshToken);
        if (responseObject == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(responseObject);
    }
}
