package com.syncria.module.pet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PetRequestDTO(
        @NotNull(message = "Contact ID is required")
        Long contactId,

        @NotBlank(message = "Name is required")
        @Size(max = 255, message = "Name must be at most 255 characters")
        String name,

        @NotBlank(message = "Species is required")
        @Size(max = 100, message = "Species must be at most 100 characters")
        String species,

        @Size(max = 100, message = "Breed must be at most 100 characters")
        String breed,

        LocalDate birthDate,

        @Size(max = 20, message = "Gender must be at most 20 characters")
        String gender,

        String notes
) {
}
