package com.example.sss001.auth.application;

import com.example.sss001.auth.domain.AuthService;
import com.example.sss001.auth.dto.SignInRequest;
import com.example.sss001.auth.dto.SignUpRequest;
import com.example.sss001.auth.dto.TokenResponse;
import com.example.sss001.user.domain.User;
import com.example.sss001.user.dto.UserDTO;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/signin")
    public TokenResponse signIn(
            @RequestBody SignInRequest request) {

        return service.signIn(request);
    }

    @PostMapping("/signup")
    public UserDTO signUp(
            @RequestBody SignUpRequest request) {

        User user = service.signUp(request);

        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getRole()
        );
    }
}