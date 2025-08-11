package org.exam.controller;

import jakarta.validation.Valid;
import org.exam.dto.request.LoginRequest;
import org.exam.dto.request.RegistrationRequest;
import org.exam.dto.response.AuthResponse;
import org.exam.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerStudent(@Valid @RequestBody RegistrationRequest request) {
        return new ResponseEntity<>(authService.registerStudent(request), HttpStatus.CREATED);
    }

    @PostMapping("/admin/login")
    public ResponseEntity<AuthResponse> adminLogin(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.adminLogin(request));
    }

    @GetMapping("/oauth2/google")
    public ResponseEntity<Void> initiateGoogleAuth() {
        String redirectUrl = authService.getGoogleAuthorizationUrl();
        return ResponseEntity.status(HttpStatus.FOUND).header("Location", redirectUrl).build();
    }

    @GetMapping("/oauth2/callback/google")
    public ResponseEntity<AuthResponse> handleGoogleCallback(@RequestParam("code") String code) {
        return ResponseEntity.ok(authService.processGoogleCallback(code));
    }
}
