package com.syncria.module.auth.service;

import com.syncria.module.auth.dto.AuthResponseDTO;
import com.syncria.module.auth.dto.LoginRequestDTO;
import com.syncria.module.auth.dto.RegisterRequestDTO;
import com.syncria.module.auth.exception.AuthException;
import com.syncria.module.auth.exception.DuplicateEmailException;
import com.syncria.module.company.entity.Company;
import com.syncria.module.company.repository.CompanyRepository;
import com.syncria.module.user.entity.User;
import com.syncria.module.user.repository.UserRepository;
import com.syncria.security.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, companyRepository, passwordEncoder, jwtUtil);
    }

    @Test
    void register_ShouldCreateCompanyAndUserAndReturnToken() {
        RegisterRequestDTO request = new RegisterRequestDTO("test@test.com", "password123", "Test User");

        when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(companyRepository.save(any(Company.class))).thenAnswer(invocation -> {
            Company c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtUtil.generateToken("test@test.com", 1L)).thenReturn("test-token");

        AuthResponseDTO response = authService.register(request);

        assertThat(response.token()).isEqualTo("test-token");
        assertThat(response.email()).isEqualTo("test@test.com");
        assertThat(response.fullName()).isEqualTo("Test User");
        assertThat(response.role()).isEqualTo("USER");
    }

    @Test
    void register_ShouldThrowDuplicateEmailException_WhenEmailExists() {
        RegisterRequestDTO request = new RegisterRequestDTO("existing@test.com", "password123", "Existing User");

        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("existing@test.com");
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() {
        LoginRequestDTO request = new LoginRequestDTO("test@test.com", "password123");
        User user = User.builder()
                .email("test@test.com")
                .password(passwordEncoder.encode("password123"))
                .fullName("Test User")
                .role("USER")
                .companyId(1L)
                .build();

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("test@test.com", 1L)).thenReturn("test-token");

        AuthResponseDTO response = authService.login(request);

        assertThat(response.token()).isEqualTo("test-token");
        assertThat(response.email()).isEqualTo("test@test.com");
    }

    @Test
    void login_ShouldThrowAuthException_WhenPasswordIsInvalid() {
        LoginRequestDTO request = new LoginRequestDTO("test@test.com", "wrongpassword");
        User user = User.builder()
                .email("test@test.com")
                .password(passwordEncoder.encode("correctpassword"))
                .fullName("Test User")
                .role("USER")
                .build();

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(AuthException.class)
                .hasMessage("Invalid email or password");
    }

    @Test
    void login_ShouldThrowAuthException_WhenUserNotFound() {
        LoginRequestDTO request = new LoginRequestDTO("nonexistent@test.com", "password123");

        when(userRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(AuthException.class)
                .hasMessage("Invalid email or password");
    }
}
