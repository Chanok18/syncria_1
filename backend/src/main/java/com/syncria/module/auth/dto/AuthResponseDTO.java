package com.syncria.module.auth.dto;

public record AuthResponseDTO(
        String token,
        String email,
        String fullName,
        String role
) {}
