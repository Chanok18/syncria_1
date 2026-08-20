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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentMapper appointmentMapper;
    private final PetRepository petRepository;
    private final ContactRepository contactRepository;

    public AppointmentResponseDTO create(AppointmentRequestDTO request, Long companyId) {
        Pet pet = petRepository.findByIdAndCompanyIdAndDeletedFalse(request.petId(), companyId)
                .orElseThrow(() -> new AppointmentNotFoundException(request.petId()));

        Contact contact = contactRepository.findByIdAndCompanyIdAndDeletedFalse(request.contactId(), companyId)
                .orElseThrow(() -> new AppointmentNotFoundException(request.contactId()));

        if (request.startTime().isAfter(request.endTime()) || request.startTime().equals(request.endTime())) {
            throw new AppointmentConflictException("Start time must be before end time");
        }

        List<Appointment> conflicts = appointmentRepository.findConflicts(
                companyId, request.petId(), request.appointmentDate(),
                request.startTime(), request.endTime(), 0L);

        if (!conflicts.isEmpty()) {
            throw new AppointmentConflictException(
                    "Time slot conflict: another appointment exists for this pet at the same time");
        }

        Appointment appointment = appointmentMapper.toEntity(request);
        appointment.setCompanyId(companyId);
        appointment.setPetId(pet.getId());
        appointment.setContactId(contact.getId());
        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointment = appointmentRepository.save(appointment);

        AppointmentResponseDTO response = appointmentMapper.toResponse(appointment);
        return enrichResponse(response, pet, contact);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponseDTO> findAll(Long companyId, AppointmentStatus status, String search, Pageable pageable) {
        return appointmentRepository.findByCompanyIdAndFilters(companyId, status, search, pageable)
                .map(a -> {
                    Pet pet = petRepository.findById(a.getPetId()).orElse(null);
                    Contact contact = contactRepository.findById(a.getContactId()).orElse(null);
                    return enrichResponse(appointmentMapper.toResponse(a), pet, contact);
                });
    }

    @Transactional(readOnly = true)
    public AppointmentResponseDTO findById(Long id, Long companyId) {
        Appointment appointment = appointmentRepository.findByIdAndCompanyIdAndDeletedFalse(id, companyId)
                .orElseThrow(() -> new AppointmentNotFoundException(id));
        Pet pet = petRepository.findById(appointment.getPetId()).orElse(null);
        Contact contact = contactRepository.findById(appointment.getContactId()).orElse(null);
        return enrichResponse(appointmentMapper.toResponse(appointment), pet, contact);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponseDTO> findByDateRange(Long companyId, LocalDate startDate, LocalDate endDate) {
        return appointmentRepository.findByCompanyIdAndAppointmentDateBetweenAndDeletedFalse(companyId, startDate, endDate)
                .stream()
                .map(a -> {
                    Pet pet = petRepository.findById(a.getPetId()).orElse(null);
                    Contact contact = contactRepository.findById(a.getContactId()).orElse(null);
                    return enrichResponse(appointmentMapper.toResponse(a), pet, contact);
                })
                .collect(Collectors.toList());
    }

    public AppointmentResponseDTO update(Long id, AppointmentRequestDTO request, Long companyId) {
        Appointment appointment = appointmentRepository.findByIdAndCompanyIdAndDeletedFalse(id, companyId)
                .orElseThrow(() -> new AppointmentNotFoundException(id));

        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new AppointmentConflictException("Only scheduled appointments can be edited");
        }

        Pet pet = petRepository.findByIdAndCompanyIdAndDeletedFalse(request.petId(), companyId)
                .orElseThrow(() -> new AppointmentNotFoundException(request.petId()));

        Contact contact = contactRepository.findByIdAndCompanyIdAndDeletedFalse(request.contactId(), companyId)
                .orElseThrow(() -> new AppointmentNotFoundException(request.contactId()));

        if (request.startTime().isAfter(request.endTime()) || request.startTime().equals(request.endTime())) {
            throw new AppointmentConflictException("Start time must be before end time");
        }

        List<Appointment> conflicts = appointmentRepository.findConflicts(
                companyId, request.petId(), request.appointmentDate(),
                request.startTime(), request.endTime(), id);

        if (!conflicts.isEmpty()) {
            throw new AppointmentConflictException(
                    "Time slot conflict: another appointment exists for this pet at the same time");
        }

        appointment.setPetId(pet.getId());
        appointment.setContactId(contact.getId());
        appointment.setTitle(request.title());
        appointment.setReason(request.reason());
        appointment.setAppointmentDate(request.appointmentDate());
        appointment.setStartTime(request.startTime());
        appointment.setEndTime(request.endTime());
        appointment.setNotes(request.notes());

        appointment = appointmentRepository.save(appointment);
        return enrichResponse(appointmentMapper.toResponse(appointment), pet, contact);
    }

    public AppointmentResponseDTO updateStatus(Long id, AppointmentStatus status, Long companyId) {
        Appointment appointment = appointmentRepository.findByIdAndCompanyIdAndDeletedFalse(id, companyId)
                .orElseThrow(() -> new AppointmentNotFoundException(id));

        if (appointment.getStatus() == AppointmentStatus.COMPLETED ||
            appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new AppointmentConflictException(
                    "Cannot change status of a " + appointment.getStatus().name().toLowerCase() + " appointment");
        }

        appointment.setStatus(status);
        appointment = appointmentRepository.save(appointment);

        Pet pet = petRepository.findById(appointment.getPetId()).orElse(null);
        Contact contact = contactRepository.findById(appointment.getContactId()).orElse(null);
        return enrichResponse(appointmentMapper.toResponse(appointment), pet, contact);
    }

    public void delete(Long id, Long companyId) {
        Appointment appointment = appointmentRepository.findByIdAndCompanyIdAndDeletedFalse(id, companyId)
                .orElseThrow(() -> new AppointmentNotFoundException(id));
        appointment.setDeleted(true);
        appointmentRepository.save(appointment);
    }

    @Transactional(readOnly = true)
    public long countByCompany(Long companyId) {
        return appointmentRepository.countByCompanyIdAndDeletedFalse(companyId);
    }

    @Transactional(readOnly = true)
    public long countByCompanyAndStatus(Long companyId, AppointmentStatus status) {
        return appointmentRepository.countByCompanyIdAndStatusAndDeletedFalse(companyId, status);
    }

    private AppointmentResponseDTO enrichResponse(AppointmentResponseDTO response, Pet pet, Contact contact) {
        if (response == null) return null;
        return new AppointmentResponseDTO(
                response.id(),
                response.companyId(),
                response.petId(),
                response.contactId(),
                pet != null ? pet.getName() : "Unknown",
                contact != null ? contact.getName() : "Unknown",
                response.title(),
                response.reason(),
                response.appointmentDate(),
                response.startTime(),
                response.endTime(),
                response.status(),
                response.notes(),
                response.createdAt(),
                response.updatedAt()
        );
    }
}
