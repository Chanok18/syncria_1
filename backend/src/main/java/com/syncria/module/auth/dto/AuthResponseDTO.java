package com.syncria.module.auth.dto;

public record AuthResponseDTO(
        String token,
        String email,
        String fullName,
        String role
) {
    public AuthResponseDTO withoutToken() {
        return new AuthResponseDTO(null, email, fullName, role);
    }
}
