package com.syncria.module.contact.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContactRequestDTO(
        @NotBlank(message = "Name is required")
        @Size(max = 255, message = "Name must be at most 255 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Size(max = 255, message = "Email must be at most 255 characters")
        String email,

        @Size(max = 50, message = "Phone must be at most 50 characters")
        String phone,

        @Size(max = 500, message = "Address must be at most 500 characters")
        String address,

        String notes
) {
}
