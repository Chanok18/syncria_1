package com.syncria.module.pet.service;

import com.syncria.module.contact.entity.Contact;
import com.syncria.module.contact.repository.ContactRepository;
import com.syncria.module.pet.dto.PetRequestDTO;
import com.syncria.module.pet.dto.PetResponseDTO;
import com.syncria.module.pet.entity.Pet;
import com.syncria.module.pet.exception.PetNotFoundException;
import com.syncria.module.pet.mapper.PetMapper;
import com.syncria.module.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PetService {

    private final PetRepository petRepository;
    private final PetMapper petMapper;
    private final ContactRepository contactRepository;

    public PetResponseDTO create(PetRequestDTO request, Long companyId) {
        Contact contact = contactRepository.findByIdAndCompanyIdAndDeletedFalse(request.contactId(), companyId)
                .orElseThrow(() -> new PetNotFoundException(request.contactId()));

        Pet pet = petMapper.toEntity(request);
        pet.setCompanyId(companyId);
        pet.setContactId(contact.getId());
        pet = petRepository.save(pet);
        return petMapper.toResponse(pet);
    }

    @Transactional(readOnly = true)
    public Page<PetResponseDTO> findAll(Long companyId, String search, Pageable pageable) {
        return petRepository.findByCompanyIdAndSearch(companyId, search, pageable)
                .map(petMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public PetResponseDTO findById(Long id, Long companyId) {
        Pet pet = petRepository.findByIdAndCompanyIdAndDeletedFalse(id, companyId)
                .orElseThrow(() -> new PetNotFoundException(id));
        return petMapper.toResponse(pet);
    }

    @Transactional(readOnly = true)
    public List<PetResponseDTO> findByContactId(Long contactId, Long companyId) {
        Contact contact = contactRepository.findByIdAndCompanyIdAndDeletedFalse(contactId, companyId)
                .orElseThrow(() -> new PetNotFoundException(contactId));
        return petRepository.findByContactIdAndCompanyIdAndDeletedFalse(contact.getId(), companyId)
                .stream()
                .map(petMapper::toResponse)
                .collect(Collectors.toList());
    }

    public PetResponseDTO update(Long id, PetRequestDTO request, Long companyId) {
        Pet pet = petRepository.findByIdAndCompanyIdAndDeletedFalse(id, companyId)
                .orElseThrow(() -> new PetNotFoundException(id));

        Contact contact = contactRepository.findByIdAndCompanyIdAndDeletedFalse(request.contactId(), companyId)
                .orElseThrow(() -> new PetNotFoundException(request.contactId()));

        pet.setContactId(contact.getId());
        pet.setName(request.name());
        pet.setSpecies(request.species());
        pet.setBreed(request.breed());
        pet.setBirthDate(request.birthDate());
        pet.setGender(request.gender());
        pet.setNotes(request.notes());

        pet = petRepository.save(pet);
        return petMapper.toResponse(pet);
    }

    public void delete(Long id, Long companyId) {
        Pet pet = petRepository.findByIdAndCompanyIdAndDeletedFalse(id, companyId)
                .orElseThrow(() -> new PetNotFoundException(id));
        pet.setDeleted(true);
        petRepository.save(pet);
    }

    @Transactional(readOnly = true)
    public long countByCompany(Long companyId) {
        return petRepository.countByCompanyIdAndDeletedFalse(companyId);
    }
}
