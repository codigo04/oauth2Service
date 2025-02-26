package com.unit.oauth2Service.controller;


import com.unit.oauth2Service.dto.request.AuthRequestDTO;
import com.unit.oauth2Service.dto.response.AuthResponseDTO;
import com.unit.oauth2Service.security.service.AuthService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthController {

    @Autowired
    private AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> loginUser(@RequestBody AuthRequestDTO authRequestDTO) throws BadRequestException {
        return new ResponseEntity<>(authService.login(authRequestDTO), HttpStatus.OK);
    }


    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register( @RequestBody AuthRequestDTO requestDTO) {
        return new ResponseEntity<>(authService.register(requestDTO), HttpStatus.OK);
    }
}
