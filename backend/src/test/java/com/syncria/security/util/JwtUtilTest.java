package com.syncria.security.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "dGVzdC1zZWNyZXQta2V5LWZvci1qd3QtdXRpbC10ZXN0LW1pbmltdW0tMzItYnl0ZXM=");
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", 3600000L);
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        String token = jwtUtil.generateToken("test@test.com", 1L);

        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    void extractEmail_ShouldReturnCorrectEmail() {
        String token = jwtUtil.generateToken("test@test.com", 1L);

        String email = jwtUtil.extractEmail(token);

        assertThat(email).isEqualTo("test@test.com");
    }

    @Test
    void extractCompanyId_ShouldReturnCorrectCompanyId() {
        String token = jwtUtil.generateToken("test@test.com", 42L);

        Long companyId = jwtUtil.extractCompanyId(token);

        assertThat(companyId).isEqualTo(42L);
    }

    @Test
    void validateToken_ShouldReturnFalse_ForInvalidToken() {
        boolean valid = jwtUtil.validateToken("invalid-token");

        assertThat(valid).isFalse();
    }

    @Test
    void validateToken_ShouldReturnFalse_ForExpiredToken() throws Exception {
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", -3600000L);
        String token = jwtUtil.generateToken("test@test.com", 1L);

        boolean valid = jwtUtil.validateToken(token);

        assertThat(valid).isFalse();
    }

    @Test
    void validateToken_ShouldReturnTrue_ForValidToken() {
        String token = jwtUtil.generateToken("test@test.com", 1L);

        boolean valid = jwtUtil.validateToken(token);

        assertThat(valid).isTrue();
    }
}
