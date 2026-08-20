package com.syncria.module.pet.controller;

import com.syncria.module.pet.dto.PetRequestDTO;
import com.syncria.module.pet.dto.PetResponseDTO;
import com.syncria.module.pet.service.PetService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @PostMapping
    public ResponseEntity<PetResponseDTO> create(
            @Valid @RequestBody PetRequestDTO request,
            Authentication authentication) {
        Long companyId = ((User) authentication.getPrincipal()).getCompanyId();
        PetResponseDTO response = petService.create(request, companyId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<PetResponseDTO>> findAll(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
            Authentication authentication) {
        Long companyId = ((User) authentication.getPrincipal()).getCompanyId();
        Page<PetResponseDTO> page = petService.findAll(companyId, search, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetResponseDTO> findById(
            @PathVariable Long id,
            Authentication authentication) {
        Long companyId = ((User) authentication.getPrincipal()).getCompanyId();
        PetResponseDTO response = petService.findById(id, companyId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/contact/{contactId}")
    public ResponseEntity<List<PetResponseDTO>> findByContactId(
            @PathVariable Long contactId,
            Authentication authentication) {
        Long companyId = ((User) authentication.getPrincipal()).getCompanyId();
        List<PetResponseDTO> pets = petService.findByContactId(contactId, companyId);
        return ResponseEntity.ok(pets);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody PetRequestDTO request,
            Authentication authentication) {
        Long companyId = ((User) authentication.getPrincipal()).getCompanyId();
        PetResponseDTO response = petService.update(id, request, companyId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Authentication authentication) {
        Long companyId = ((User) authentication.getPrincipal()).getCompanyId();
        petService.delete(id, companyId);
        return ResponseEntity.noContent().build();
    }
}
