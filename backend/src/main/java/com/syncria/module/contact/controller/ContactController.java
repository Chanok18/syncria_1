package com.syncria.module.contact.controller;

import com.syncria.module.contact.dto.ContactRequestDTO;
import com.syncria.module.contact.dto.ContactResponseDTO;
import com.syncria.module.contact.service.ContactService;
import com.syncria.module.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

@RestController
@RequestMapping("/api/v1/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<ContactResponseDTO> create(
            @Valid @RequestBody ContactRequestDTO request,
            Authentication authentication) {
        Long companyId = ((User) authentication.getPrincipal()).getCompanyId();
        ContactResponseDTO response = contactService.create(request, companyId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<ContactResponseDTO>> findAll(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
            Authentication authentication) {
        Long companyId = ((User) authentication.getPrincipal()).getCompanyId();
        Page<ContactResponseDTO> page = contactService.findAll(companyId, search, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactResponseDTO> findById(
            @PathVariable Long id,
            Authentication authentication) {
        Long companyId = ((User) authentication.getPrincipal()).getCompanyId();
        ContactResponseDTO response = contactService.findById(id, companyId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContactResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ContactRequestDTO request,
            Authentication authentication) {
        Long companyId = ((User) authentication.getPrincipal()).getCompanyId();
        ContactResponseDTO response = contactService.update(id, request, companyId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Authentication authentication) {
        Long companyId = ((User) authentication.getPrincipal()).getCompanyId();
        contactService.delete(id, companyId);
        return ResponseEntity.noContent().build();
    }
}
