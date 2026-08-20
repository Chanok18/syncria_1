package com.syncria.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    @PostConstruct
    public void validateSecret() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET is not configured. Set app.jwt.secret or JWT_SECRET environment variable.");
        }
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secret);
            if (keyBytes.length < 32) {
                throw new IllegalStateException(
                        "JWT_SECRET must be at least 256 bits (32 bytes) when base64-decoded. " +
                        "Current length: " + (keyBytes.length * 8) + " bits. " +
                        "Generate a secure key with: openssl rand -base64 32");
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(
                    "JWT_SECRET is not a valid Base64-encoded string. " +
                    "Generate a secure key with: openssl rand -base64 32", e);
        }
    }

    public String generateToken(String email, Long companyId) {
        return Jwts.builder()
                .subject(email)
                .claim("companyId", companyId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public Long extractCompanyId(String token) {
        return parseClaims(token).get("companyId", Long.class);
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException | ExpiredJwtException |
                 UnsupportedJwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
