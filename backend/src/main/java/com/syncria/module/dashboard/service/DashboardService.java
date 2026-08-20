package com.syncria.module.dashboard.service;

import com.syncria.module.appointment.entity.Appointment;
import com.syncria.module.appointment.entity.AppointmentStatus;
import com.syncria.module.appointment.repository.AppointmentRepository;
import com.syncria.module.contact.repository.ContactRepository;
import com.syncria.module.dashboard.dto.DashboardResponseDTO;
import com.syncria.module.pet.entity.Pet;
import com.syncria.module.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final ContactRepository contactRepository;
    private final PetRepository petRepository;
    private final AppointmentRepository appointmentRepository;

    public DashboardResponseDTO getDashboard(Long companyId) {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = weekStart.plusDays(6);

        long totalClients = contactRepository.countByCompanyIdAndDeletedFalse(companyId);
        long totalPets = petRepository.countByCompanyIdAndDeletedFalse(companyId);

        long todayAppointments = appointmentRepository
                .countByCompanyIdAndAppointmentDateAndDeletedFalse(companyId, today);

        List<Appointment> weekAppointments = appointmentRepository
                .findByCompanyIdAndAppointmentDateBetweenAndDeletedFalse(companyId, weekStart, weekEnd);

        long scheduledAppointments = appointmentRepository
                .countByCompanyIdAndStatusAndDeletedFalse(companyId, AppointmentStatus.SCHEDULED);
        long completedAppointments = appointmentRepository
                .countByCompanyIdAndStatusAndDeletedFalse(companyId, AppointmentStatus.COMPLETED);
        long cancelledAppointments = appointmentRepository
                .countByCompanyIdAndStatusAndDeletedFalse(companyId, AppointmentStatus.CANCELLED);

        List<Appointment> todayAppointmentsList = appointmentRepository
                .findByCompanyIdAndAppointmentDateAndDeletedFalse(companyId, today);

        List<DashboardResponseDTO.RecentAppointment> recentAppointments = todayAppointmentsList.stream()
                .sorted((a, b) -> a.getStartTime().compareTo(b.getStartTime()))
                .limit(5)
                .map(this::toRecentAppointment)
                .collect(Collectors.toList());

        List<Pet> allPets = petRepository.findByCompanyIdAndDeletedFalse(companyId);
        List<DashboardResponseDTO.SpeciesCount> speciesDistribution = calculateSpeciesDistribution(allPets);

        return new DashboardResponseDTO(
                totalClients,
                totalPets,
                todayAppointments,
                weekAppointments.size(),
                scheduledAppointments,
                completedAppointments,
                cancelledAppointments,
                recentAppointments,
                speciesDistribution
        );
    }

    private DashboardResponseDTO.RecentAppointment toRecentAppointment(Appointment appointment) {
        return new DashboardResponseDTO.RecentAppointment(
                appointment.getId(),
                "",
                "",
                appointment.getTitle(),
                appointment.getAppointmentDate(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getStatus()
        );
    }

    private List<DashboardResponseDTO.SpeciesCount> calculateSpeciesDistribution(List<Pet> pets) {
        Map<String, Long> speciesMap = new LinkedHashMap<>();
        for (Pet pet : pets) {
            speciesMap.merge(pet.getSpecies(), 1L, Long::sum);
        }
        return speciesMap.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .map(entry -> new DashboardResponseDTO.SpeciesCount(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
