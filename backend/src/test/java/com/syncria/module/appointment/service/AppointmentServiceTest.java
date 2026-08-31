package com.syncria.module.appointment.service;

import com.syncria.module.appointment.dto.AppointmentRequestDTO;
import com.syncria.module.appointment.dto.AppointmentResponseDTO;
import com.syncria.module.appointment.entity.Appointment;
import com.syncria.module.appointment.entity.AppointmentStatus;
import com.syncria.module.appointment.exception.AppointmentConflictException;
import com.syncria.module.appointment.exception.AppointmentNotFoundException;
import com.syncria.module.appointment.mapper.AppointmentMapper;
import com.syncria.module.appointment.repository.AppointmentRepository;
import com.syncria.module.contact.entity.Contact;
import com.syncria.module.contact.repository.ContactRepository;
import com.syncria.module.pet.entity.Pet;
import com.syncria.module.pet.repository.PetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PetRepository petRepository;

    @Mock
    private ContactRepository contactRepository;

    private final AppointmentMapper appointmentMapper = Mappers.getMapper(AppointmentMapper.class);
    private AppointmentService appointmentService;

    private static final Long COMPANY_ID = 1L;
    private static final Long PET_ID = 10L;
    private static final Long CONTACT_ID = 20L;
    private static final Long APPOINTMENT_ID = 100L;

    @BeforeEach
    void setUp() {
        appointmentService = new AppointmentService(appointmentRepository, appointmentMapper, petRepository, contactRepository);
    }

    private Pet buildPet() {
        return Pet.builder().id(PET_ID).companyId(COMPANY_ID).name("Buddy").species("Dog").build();
    }

    private Contact buildContact() {
        return Contact.builder().id(CONTACT_ID).companyId(COMPANY_ID).name("John Doe").email("john@test.com").build();
    }

    private Appointment buildAppointment() {
        return Appointment.builder()
                .id(APPOINTMENT_ID)
                .companyId(COMPANY_ID)
                .petId(PET_ID)
                .contactId(CONTACT_ID)
                .title("Checkup")
                .reason("Annual checkup")
                .appointmentDate(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(11, 0))
                .status(AppointmentStatus.SCHEDULED)
                .deleted(false)
                .build();
    }

    private AppointmentRequestDTO buildRequest() {
        return new AppointmentRequestDTO(
                PET_ID, CONTACT_ID, "Checkup", "Annual checkup",
                LocalDate.now().plusDays(1), LocalTime.of(10, 0), LocalTime.of(11, 0),
                null, null);
    }

    @Test
    void create_ShouldReturnAppointmentResponseDTO() {
        when(petRepository.findByIdAndCompanyIdAndDeletedFalse(PET_ID, COMPANY_ID))
                .thenReturn(Optional.of(buildPet()));
        when(contactRepository.findByIdAndCompanyIdAndDeletedFalse(CONTACT_ID, COMPANY_ID))
                .thenReturn(Optional.of(buildContact()));
        when(appointmentRepository.findConflicts(any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of());
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
            Appointment a = invocation.getArgument(0);
            a.setId(APPOINTMENT_ID);
            return a;
        });

        AppointmentResponseDTO response = appointmentService.create(buildRequest(), COMPANY_ID);

        assertThat(response.id()).isEqualTo(APPOINTMENT_ID);
        assertThat(response.title()).isEqualTo("Checkup");
        assertThat(response.status()).isEqualTo(AppointmentStatus.SCHEDULED);
        assertThat(response.petName()).isEqualTo("Buddy");
        assertThat(response.contactName()).isEqualTo("John Doe");
    }

    @Test
    void create_ShouldThrowConflict_WhenTimeSlotOverlaps() {
        when(petRepository.findByIdAndCompanyIdAndDeletedFalse(PET_ID, COMPANY_ID))
                .thenReturn(Optional.of(buildPet()));
        when(contactRepository.findByIdAndCompanyIdAndDeletedFalse(CONTACT_ID, COMPANY_ID))
                .thenReturn(Optional.of(buildContact()));
        when(appointmentRepository.findConflicts(any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(buildAppointment()));

        assertThatThrownBy(() -> appointmentService.create(buildRequest(), COMPANY_ID))
                .isInstanceOf(AppointmentConflictException.class)
                .hasMessageContaining("conflict");
    }

    @Test
    void create_ShouldThrowConflict_WhenStartTimeAfterEndTime() {
        when(petRepository.findByIdAndCompanyIdAndDeletedFalse(PET_ID, COMPANY_ID))
                .thenReturn(Optional.of(buildPet()));
        when(contactRepository.findByIdAndCompanyIdAndDeletedFalse(CONTACT_ID, COMPANY_ID))
                .thenReturn(Optional.of(buildContact()));

        AppointmentRequestDTO badRequest = new AppointmentRequestDTO(
                PET_ID, CONTACT_ID, "Checkup", null,
                LocalDate.now().plusDays(1), LocalTime.of(11, 0), LocalTime.of(10, 0),
                null, null);

        assertThatThrownBy(() -> appointmentService.create(badRequest, COMPANY_ID))
                .isInstanceOf(AppointmentConflictException.class)
                .hasMessageContaining("Start time must be before end time");
    }

    @Test
    void findAll_ShouldUseDerivedQuery_WhenNoFilters() {
        Appointment appointment = buildAppointment();
        when(appointmentRepository.findByCompanyIdAndDeletedFalse(COMPANY_ID, PageRequest.of(0, 20)))
                .thenReturn(new PageImpl<>(List.of(appointment)));
        when(petRepository.findById(PET_ID)).thenReturn(Optional.of(buildPet()));
        when(contactRepository.findById(CONTACT_ID)).thenReturn(Optional.of(buildContact()));

        Page<AppointmentResponseDTO> result = appointmentService.findAll(COMPANY_ID, null, null, PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).title()).isEqualTo("Checkup");
        verify(appointmentRepository).findByCompanyIdAndDeletedFalse(COMPANY_ID, PageRequest.of(0, 20));
    }

    @Test
    void findAll_ShouldUseSearchQuery_WhenSearchProvided() {
        Appointment appointment = buildAppointment();
        when(appointmentRepository.searchByCompanyId(COMPANY_ID, "check", PageRequest.of(0, 20)))
                .thenReturn(new PageImpl<>(List.of(appointment)));
        when(petRepository.findById(PET_ID)).thenReturn(Optional.of(buildPet()));
        when(contactRepository.findById(CONTACT_ID)).thenReturn(Optional.of(buildContact()));

        Page<AppointmentResponseDTO> result = appointmentService.findAll(COMPANY_ID, null, "check", PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
        verify(appointmentRepository).searchByCompanyId(COMPANY_ID, "check", PageRequest.of(0, 20));
    }

    @Test
    void findAll_ShouldUseStatusQuery_WhenStatusProvided() {
        Appointment appointment = buildAppointment();
        when(appointmentRepository.findByCompanyIdAndDeletedFalseAndStatus(COMPANY_ID, AppointmentStatus.SCHEDULED, PageRequest.of(0, 20)))
                .thenReturn(new PageImpl<>(List.of(appointment)));
        when(petRepository.findById(PET_ID)).thenReturn(Optional.of(buildPet()));
        when(contactRepository.findById(CONTACT_ID)).thenReturn(Optional.of(buildContact()));

        Page<AppointmentResponseDTO> result = appointmentService.findAll(COMPANY_ID, AppointmentStatus.SCHEDULED, null, PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
        verify(appointmentRepository).findByCompanyIdAndDeletedFalseAndStatus(COMPANY_ID, AppointmentStatus.SCHEDULED, PageRequest.of(0, 20));
    }

    @Test
    void findAll_ShouldUseSearchAndStatusQuery_WhenBothProvided() {
        Appointment appointment = buildAppointment();
        when(appointmentRepository.searchByCompanyIdAndStatus(COMPANY_ID, AppointmentStatus.SCHEDULED, "check", PageRequest.of(0, 20)))
                .thenReturn(new PageImpl<>(List.of(appointment)));
        when(petRepository.findById(PET_ID)).thenReturn(Optional.of(buildPet()));
        when(contactRepository.findById(CONTACT_ID)).thenReturn(Optional.of(buildContact()));

        Page<AppointmentResponseDTO> result = appointmentService.findAll(COMPANY_ID, AppointmentStatus.SCHEDULED, "check", PageRequest.of(0, 20));

        assertThat(result.getContent()).hasSize(1);
        verify(appointmentRepository).searchByCompanyIdAndStatus(COMPANY_ID, AppointmentStatus.SCHEDULED, "check", PageRequest.of(0, 20));
    }

    @Test
    void findById_ShouldReturnAppointment() {
        when(appointmentRepository.findByIdAndCompanyIdAndDeletedFalse(APPOINTMENT_ID, COMPANY_ID))
                .thenReturn(Optional.of(buildAppointment()));
        when(petRepository.findById(PET_ID)).thenReturn(Optional.of(buildPet()));
        when(contactRepository.findById(CONTACT_ID)).thenReturn(Optional.of(buildContact()));

        AppointmentResponseDTO response = appointmentService.findById(APPOINTMENT_ID, COMPANY_ID);

        assertThat(response.title()).isEqualTo("Checkup");
        assertThat(response.status()).isEqualTo(AppointmentStatus.SCHEDULED);
    }

    @Test
    void findById_ShouldThrowNotFoundException_WhenNotFound() {
        when(appointmentRepository.findByIdAndCompanyIdAndDeletedFalse(99L, COMPANY_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.findById(99L, COMPANY_ID))
                .isInstanceOf(AppointmentNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void updateStatus_ShouldChangeStatusToCompleted() {
        Appointment appointment = buildAppointment();
        when(appointmentRepository.findByIdAndCompanyIdAndDeletedFalse(APPOINTMENT_ID, COMPANY_ID))
                .thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(petRepository.findById(PET_ID)).thenReturn(Optional.of(buildPet()));
        when(contactRepository.findById(CONTACT_ID)).thenReturn(Optional.of(buildContact()));

        AppointmentResponseDTO response = appointmentService.updateStatus(
                APPOINTMENT_ID, AppointmentStatus.COMPLETED, COMPANY_ID);

        assertThat(response.status()).isEqualTo(AppointmentStatus.COMPLETED);
    }

    @Test
    void updateStatus_ShouldThrowConflict_WhenAlreadyCompleted() {
        Appointment appointment = buildAppointment();
        appointment.setStatus(AppointmentStatus.COMPLETED);
        when(appointmentRepository.findByIdAndCompanyIdAndDeletedFalse(APPOINTMENT_ID, COMPANY_ID))
                .thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> appointmentService.updateStatus(
                APPOINTMENT_ID, AppointmentStatus.CANCELLED, COMPANY_ID))
                .isInstanceOf(AppointmentConflictException.class)
                .hasMessageContaining("Cannot change status");
    }

    @Test
    void update_ShouldThrowConflict_WhenNotScheduled() {
        Appointment appointment = buildAppointment();
        appointment.setStatus(AppointmentStatus.COMPLETED);
        when(appointmentRepository.findByIdAndCompanyIdAndDeletedFalse(APPOINTMENT_ID, COMPANY_ID))
                .thenReturn(Optional.of(appointment));

        assertThatThrownBy(() -> appointmentService.update(APPOINTMENT_ID, buildRequest(), COMPANY_ID))
                .isInstanceOf(AppointmentConflictException.class)
                .hasMessageContaining("Only scheduled appointments can be edited");
    }

    @Test
    void delete_ShouldSetDeletedTrue() {
        Appointment appointment = buildAppointment();
        when(appointmentRepository.findByIdAndCompanyIdAndDeletedFalse(APPOINTMENT_ID, COMPANY_ID))
                .thenReturn(Optional.of(appointment));

        appointmentService.delete(APPOINTMENT_ID, COMPANY_ID);

        assertThat(appointment.getDeleted()).isTrue();
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void delete_ShouldThrowNotFoundException_WhenNotFound() {
        when(appointmentRepository.findByIdAndCompanyIdAndDeletedFalse(99L, COMPANY_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> appointmentService.delete(99L, COMPANY_ID))
                .isInstanceOf(AppointmentNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void countByCompany_ShouldReturnCount() {
        when(appointmentRepository.countByCompanyIdAndDeletedFalse(COMPANY_ID)).thenReturn(3L);

        long count = appointmentService.countByCompany(COMPANY_ID);

        assertThat(count).isEqualTo(3L);
    }

    @Test
    void countByCompanyAndStatus_ShouldReturnCount() {
        when(appointmentRepository.countByCompanyIdAndStatusAndDeletedFalse(COMPANY_ID, AppointmentStatus.SCHEDULED))
                .thenReturn(2L);

        long count = appointmentService.countByCompanyAndStatus(COMPANY_ID, AppointmentStatus.SCHEDULED);

        assertThat(count).isEqualTo(2L);
    }
}
