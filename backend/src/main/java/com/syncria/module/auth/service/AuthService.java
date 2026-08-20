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
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }

        Company company = Company.builder()
                .name("Mi Clinica")
                .build();
        company = companyRepository.save(company);

        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .role("USER")
                .companyId(company.getId())
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getCompanyId());

        return new AuthResponseDTO(
                token,
                user.getEmail(),
                user.getFullName(),
                user.getRole()
        );
    }

    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AuthException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new AuthException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getCompanyId());

        return new AuthResponseDTO(
                token,
                user.getEmail(),
                user.getFullName(),
                user.getRole()
        );
    }
}
