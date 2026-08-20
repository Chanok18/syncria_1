package com.syncria.module.dashboard.service;

import com.syncria.module.appointment.entity.Appointment;
import com.syncria.module.appointment.entity.AppointmentStatus;
import com.syncria.module.appointment.repository.AppointmentRepository;
import com.syncria.module.contact.repository.ContactRepository;
import com.syncria.module.dashboard.dto.DashboardResponseDTO;
import com.syncria.module.pet.entity.Pet;
import com.syncria.module.pet.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        dashboardService = new DashboardService(contactRepository, petRepository, appointmentRepository);
    }

    @Test
    void getDashboard_ShouldReturnDashboardResponse() {
        Long companyId = 1L;

        when(contactRepository.countByCompanyIdAndDeletedFalse(companyId)).thenReturn(45L);
        when(petRepository.countByCompanyIdAndDeletedFalse(companyId)).thenReturn(78L);
        when(appointmentRepository.countByCompanyIdAndAppointmentDateAndDeletedFalse(eq(companyId), any(LocalDate.class)))
                .thenReturn(3L);
        when(appointmentRepository.findByCompanyIdAndAppointmentDateBetweenAndDeletedFalse(
                eq(companyId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of());
        when(appointmentRepository.countByCompanyIdAndStatusAndDeletedFalse(companyId, AppointmentStatus.SCHEDULED))
                .thenReturn(8L);
        when(appointmentRepository.countByCompanyIdAndStatusAndDeletedFalse(companyId, AppointmentStatus.COMPLETED))
                .thenReturn(3L);
        when(appointmentRepository.countByCompanyIdAndStatusAndDeletedFalse(companyId, AppointmentStatus.CANCELLED))
                .thenReturn(1L);
        when(appointmentRepository.findByCompanyIdAndAppointmentDateAndDeletedFalse(eq(companyId), any(LocalDate.class)))
                .thenReturn(List.of());
        when(petRepository.findByCompanyIdAndDeletedFalse(companyId)).thenReturn(List.of());

        DashboardResponseDTO dashboard = dashboardService.getDashboard(companyId);

        assertThat(dashboard.totalClients()).isEqualTo(45L);
        assertThat(dashboard.totalPets()).isEqualTo(78L);
        assertThat(dashboard.scheduledAppointments()).isEqualTo(8L);
        assertThat(dashboard.completedAppointments()).isEqualTo(3L);
        assertThat(dashboard.cancelledAppointments()).isEqualTo(1L);
    }

    @Test
    void getDashboard_ShouldReturnRecentAppointments() {
        Long companyId = 1L;
        LocalDate today = LocalDate.now();

        Appointment appointment1 = Appointment.builder()
                .id(1L)
                .companyId(companyId)
                .petId(1L)
                .contactId(1L)
                .title("Checkup")
                .appointmentDate(today)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(11, 0))
                .status(AppointmentStatus.SCHEDULED)
                .build();

        Appointment appointment2 = Appointment.builder()
                .id(2L)
                .companyId(companyId)
                .petId(2L)
                .contactId(2L)
                .title("Vaccination")
                .appointmentDate(today)
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(15, 0))
                .status(AppointmentStatus.COMPLETED)
                .build();

        when(contactRepository.countByCompanyIdAndDeletedFalse(companyId)).thenReturn(10L);
        when(petRepository.countByCompanyIdAndDeletedFalse(companyId)).thenReturn(20L);
        when(appointmentRepository.countByCompanyIdAndAppointmentDateAndDeletedFalse(eq(companyId), any(LocalDate.class)))
                .thenReturn(2L);
        when(appointmentRepository.findByCompanyIdAndAppointmentDateBetweenAndDeletedFalse(
                eq(companyId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of());
        when(appointmentRepository.countByCompanyIdAndStatusAndDeletedFalse(eq(companyId), any(AppointmentStatus.class)))
                .thenReturn(0L);
        when(appointmentRepository.findByCompanyIdAndAppointmentDateAndDeletedFalse(eq(companyId), any(LocalDate.class)))
                .thenReturn(List.of(appointment1, appointment2));
        when(petRepository.findByCompanyIdAndDeletedFalse(companyId)).thenReturn(List.of());

        DashboardResponseDTO dashboard = dashboardService.getDashboard(companyId);

        assertThat(dashboard.recentAppointments()).hasSize(2);
        assertThat(dashboard.recentAppointments().get(0).title()).isEqualTo("Checkup");
        assertThat(dashboard.recentAppointments().get(1).title()).isEqualTo("Vaccination");
    }

    @Test
    void getDashboard_ShouldReturnSpeciesDistribution() {
        Long companyId = 1L;

        Pet dog1 = Pet.builder().id(1L).companyId(companyId).name("Buddy").species("Dog").build();
        Pet dog2 = Pet.builder().id(2L).companyId(companyId).name("Max").species("Dog").build();
        Pet cat1 = Pet.builder().id(3L).companyId(companyId).name("Luna").species("Cat").build();

        when(contactRepository.countByCompanyIdAndDeletedFalse(companyId)).thenReturn(5L);
        when(petRepository.countByCompanyIdAndDeletedFalse(companyId)).thenReturn(3L);
        when(appointmentRepository.countByCompanyIdAndAppointmentDateAndDeletedFalse(eq(companyId), any(LocalDate.class)))
                .thenReturn(0L);
        when(appointmentRepository.findByCompanyIdAndAppointmentDateBetweenAndDeletedFalse(
                eq(companyId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of());
        when(appointmentRepository.countByCompanyIdAndStatusAndDeletedFalse(eq(companyId), any(AppointmentStatus.class)))
                .thenReturn(0L);
        when(appointmentRepository.findByCompanyIdAndAppointmentDateAndDeletedFalse(eq(companyId), any(LocalDate.class)))
                .thenReturn(List.of());
        when(petRepository.findByCompanyIdAndDeletedFalse(companyId)).thenReturn(List.of(dog1, dog2, cat1));

        DashboardResponseDTO dashboard = dashboardService.getDashboard(companyId);

        assertThat(dashboard.speciesDistribution()).hasSize(2);
        assertThat(dashboard.speciesDistribution().get(0).species()).isEqualTo("Dog");
        assertThat(dashboard.speciesDistribution().get(0).count()).isEqualTo(2L);
        assertThat(dashboard.speciesDistribution().get(1).species()).isEqualTo("Cat");
        assertThat(dashboard.speciesDistribution().get(1).count()).isEqualTo(1L);
    }

    @Test
    void getDashboard_ShouldReturnEmptyWhenNoData() {
        Long companyId = 99L;

        when(contactRepository.countByCompanyIdAndDeletedFalse(companyId)).thenReturn(0L);
        when(petRepository.countByCompanyIdAndDeletedFalse(companyId)).thenReturn(0L);
        when(appointmentRepository.countByCompanyIdAndAppointmentDateAndDeletedFalse(eq(companyId), any(LocalDate.class)))
                .thenReturn(0L);
        when(appointmentRepository.findByCompanyIdAndAppointmentDateBetweenAndDeletedFalse(
                eq(companyId), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of());
        when(appointmentRepository.countByCompanyIdAndStatusAndDeletedFalse(eq(companyId), any(AppointmentStatus.class)))
                .thenReturn(0L);
        when(appointmentRepository.findByCompanyIdAndAppointmentDateAndDeletedFalse(eq(companyId), any(LocalDate.class)))
                .thenReturn(List.of());
        when(petRepository.findByCompanyIdAndDeletedFalse(companyId)).thenReturn(List.of());

        DashboardResponseDTO dashboard = dashboardService.getDashboard(companyId);

        assertThat(dashboard.totalClients()).isEqualTo(0L);
        assertThat(dashboard.totalPets()).isEqualTo(0L);
        assertThat(dashboard.todayAppointments()).isEqualTo(0L);
        assertThat(dashboard.recentAppointments()).isEmpty();
        assertThat(dashboard.speciesDistribution()).isEmpty();
    }
}
