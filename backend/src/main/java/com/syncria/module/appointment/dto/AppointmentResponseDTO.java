package com.syncria.module.appointment.dto;

import com.syncria.module.appointment.entity.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record AppointmentResponseDTO(
        Long id,
        Long companyId,
        Long petId,
        Long contactId,
        String petName,
        String contactName,
        String title,
        String reason,
        LocalDate appointmentDate,
        LocalTime startTime,
        LocalTime endTime,
        AppointmentStatus status,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
