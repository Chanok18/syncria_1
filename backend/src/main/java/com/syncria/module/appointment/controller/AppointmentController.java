package com.syncria.module.appointment.controller;

import com.syncria.module.appointment.dto.AppointmentRequestDTO;
import com.syncria.module.appointment.dto.AppointmentResponseDTO;
import com.syncria.module.appointment.entity.AppointmentStatus;
import com.syncria.module.appointment.service.AppointmentService;
import com.syncria.module.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> create(
            @Valid @RequestBody AppointmentRequestDTO request,
            Authentication authentication) {
        Long companyId = ((User) authentication.getPrincipal()).getCompanyId();
        AppointmentResponseDTO response = appointmentService.create(request, companyId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<AppointmentResponseDTO>> findAll(
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "appointmentDate", direction = Sort.Direction.ASC) Pageable pageable,
            Authentication authentication) {
        Long companyId = ((User) authentication.getPrincipal()).getCompanyId();
        Page<AppointmentResponseDTO> page = appointmentService.findAll(companyId, status, search, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> findById(
            @PathVariable Long id,
            Authentication authentication) {
        Long companyId = ((User) authentication.getPrincipal()).getCompanyId();
        AppointmentResponseDTO response = appointmentService.findById(id, companyId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/range")
    public ResponseEntity<List<AppointmentResponseDTO>> findByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication) {
        Long companyId = ((User) authentication.getPrincipal()).getCompanyId();
        List<AppointmentResponseDTO> appointments = appointmentService.findByDateRange(companyId, startDate, endDate);
        return ResponseEntity.ok(appointments);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentRequestDTO request,
            Authentication authentication) {
        Long companyId = ((User) authentication.getPrincipal()).getCompanyId();
        AppointmentResponseDTO response = appointmentService.update(id, request, companyId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AppointmentResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            Authentication authentication) {
        Long companyId = ((User) authentication.getPrincipal()).getCompanyId();
        AppointmentStatus status = AppointmentStatus.valueOf(body.get("status"));
        AppointmentResponseDTO response = appointmentService.updateStatus(id, status, companyId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Authentication authentication) {
        Long companyId = ((User) authentication.getPrincipal()).getCompanyId();
        appointmentService.delete(id, companyId);
        return ResponseEntity.noContent().build();
    }
}
