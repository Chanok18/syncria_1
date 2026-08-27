package com.syncria.module.auth.controller;

import com.syncria.module.auth.dto.AuthResponseDTO;
import com.syncria.module.auth.dto.LoginRequestDTO;
import com.syncria.module.auth.dto.RegisterRequestDTO;
import com.syncria.module.auth.service.AuthService;
import com.syncria.security.filter.RateLimitingFilter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
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
    private final RateLimitingFilter rateLimitingFilter;

    @Value("${app.jwt.cookie-max-age:86400}")
    private long cookieMaxAge;

    @Value("${app.jwt.cookie-secure:false}")
    private boolean cookieSecure;

    @Value("${app.jwt.cookie-domain:}")
    private String cookieDomain;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request,
                                                    HttpServletResponse response) {
        AuthResponseDTO fullResponse = authService.register(request);
        addTokenCookie(response, fullResponse.token());
        return ResponseEntity.status(HttpStatus.CREATED).body(fullResponse.withoutToken());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request,
                                                 HttpServletResponse response) {
        if (rateLimitingFilter.isRateLimitedByLoginEmail(request.email())) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(null);
        }

        AuthResponseDTO fullResponse = authService.login(request);
        addTokenCookie(response, fullResponse.token());
        return ResponseEntity.ok(fullResponse.withoutToken());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();
        if (cookieDomain != null && !cookieDomain.isBlank()) {
            cookie = ResponseCookie.from("token", "")
                    .httpOnly(true)
                    .secure(cookieSecure)
                    .sameSite("Lax")
                    .path("/")
                    .domain(cookieDomain)
                    .maxAge(0)
                    .build();
        }
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponseDTO> me(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof com.syncria.module.user.entity.User)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        com.syncria.module.user.entity.User user =
                (com.syncria.module.user.entity.User) authentication.getPrincipal();
        return ResponseEntity.ok(new AuthResponseDTO(
                null,
                user.getEmail(),
                user.getFullName(),
                user.getRole()
        ));
    }

    private void addTokenCookie(HttpServletResponse response, String token) {
        ResponseCookie.ResponseCookieBuilder builder = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path("/")
                .maxAge(cookieMaxAge);

        if (cookieDomain != null && !cookieDomain.isBlank()) {
            builder.domain(cookieDomain);
        }

        response.addHeader(HttpHeaders.SET_COOKIE, builder.build().toString());
    }
}
