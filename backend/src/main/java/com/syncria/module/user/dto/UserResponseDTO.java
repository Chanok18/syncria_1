package com.syncria.module.user.dto;

import java.time.LocalDateTime;

public record UserResponseDTO(
        Long id,
        String email,
        String fullName,
        String role,
        Long companyId,
        LocalDateTime createdAt
) {}
