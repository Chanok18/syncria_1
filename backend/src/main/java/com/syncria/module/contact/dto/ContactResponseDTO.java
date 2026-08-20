package com.syncria.module.contact.dto;

import java.time.LocalDateTime;

public record ContactResponseDTO(
        Long id,
        Long companyId,
        String name,
        String email,
        String phone,
        String address,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
