package com.syncria.module.pet.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PetResponseDTO(
        Long id,
        Long companyId,
        Long contactId,
        String name,
        String species,
        String breed,
        LocalDate birthDate,
        String gender,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
