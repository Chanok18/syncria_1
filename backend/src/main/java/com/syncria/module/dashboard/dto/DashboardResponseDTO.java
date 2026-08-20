package com.syncria.module.dashboard.dto;

import com.syncria.module.appointment.entity.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record DashboardResponseDTO(
        long totalClients,
        long totalPets,
        long todayAppointments,
        long weekAppointments,
        long scheduledAppointments,
        long completedAppointments,
        long cancelledAppointments,
        List<RecentAppointment> recentAppointments,
        List<SpeciesCount> speciesDistribution
) {
    public record RecentAppointment(
            Long id,
            String petName,
            String contactName,
            String title,
            LocalDate appointmentDate,
            LocalTime startTime,
            LocalTime endTime,
            AppointmentStatus status
    ) {}

    public record SpeciesCount(
            String species,
            long count
    ) {}
}
