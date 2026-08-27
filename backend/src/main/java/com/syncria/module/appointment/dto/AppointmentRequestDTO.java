package com.syncria.module.appointment.dto;

import com.syncria.module.appointment.entity.AppointmentStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentRequestDTO(
        @NotNull(message = "Pet ID is required")
        Long petId,

        @NotNull(message = "Contact ID is required")
        Long contactId,

        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title must be at most 255 characters")
        String title,

        @Size(max = 500, message = "Reason must be at most 500 characters")
        String reason,

        @NotNull(message = "Date is required")
        @Future(message = "Appointment date must be in the future")
        LocalDate appointmentDate,

        @NotNull(message = "Start time is required")
        LocalTime startTime,

        @NotNull(message = "End time is required")
        LocalTime endTime,

        AppointmentStatus status,

        @Size(max = 1000, message = "Notes must be at most 1000 characters")
        String notes
) {
}
