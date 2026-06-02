package com.example.ILovePdf.controller;

import com.example.ILovePdf.dto.LoginRequest;
import com.example.ILovePdf.dto.SignupRequest;
import com.example.ILovePdf.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(
            @RequestBody SignupRequest request) {

        authService.signup(request);

        return ResponseEntity.ok(
                "User created"
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request) {

        String token =
                authService.login(request);

        return ResponseEntity.ok(
                new AuthResponse(token)
        );
    }

    public record AuthResponse(
            String token
    ) {
    }
}
