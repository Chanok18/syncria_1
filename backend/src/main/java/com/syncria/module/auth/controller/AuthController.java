package com.syncria.module.auth.controller;

import com.syncria.module.auth.dto.AuthResponseDTO;
import com.syncria.module.auth.dto.LoginRequestDTO;
import com.syncria.module.auth.dto.RegisterRequestDTO;
import com.syncria.module.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) {
        AuthResponseDTO response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponseDTO> me(Authentication authentication) {
        com.syncria.module.user.entity.User user =
                (com.syncria.module.user.entity.User) authentication.getPrincipal();
        return ResponseEntity.ok(new AuthResponseDTO(
                null,
                user.getEmail(),
                user.getFullName(),
                user.getRole()
        ));
    }
}
