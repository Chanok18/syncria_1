package com.syncria.module.contact.service;

import com.syncria.module.contact.dto.ContactRequestDTO;
import com.syncria.module.contact.dto.ContactResponseDTO;
import com.syncria.module.contact.entity.Contact;
import com.syncria.module.contact.exception.ContactNotFoundException;
import com.syncria.module.contact.exception.DuplicateContactException;
import com.syncria.module.contact.mapper.ContactMapper;
import com.syncria.module.contact.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ContactService {

    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;

    public ContactResponseDTO create(ContactRequestDTO request, Long companyId) {
        if (contactRepository.existsByCompanyIdAndEmailAndDeletedFalse(companyId, request.email())) {
            throw new DuplicateContactException(request.email());
        }
        Contact contact = contactMapper.toEntity(request);
        contact.setCompanyId(companyId);
        contact = contactRepository.save(contact);
        return contactMapper.toResponse(contact);
    }

    @Transactional(readOnly = true)
    public Page<ContactResponseDTO> findAll(Long companyId, String search, Pageable pageable) {
        return contactRepository.findByCompanyIdAndSearch(companyId, search, pageable)
                .map(contactMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ContactResponseDTO findById(Long id, Long companyId) {
        Contact contact = contactRepository.findByIdAndCompanyIdAndDeletedFalse(id, companyId)
                .orElseThrow(() -> new ContactNotFoundException(id));
        return contactMapper.toResponse(contact);
    }

    public ContactResponseDTO update(Long id, ContactRequestDTO request, Long companyId) {
        Contact contact = contactRepository.findByIdAndCompanyIdAndDeletedFalse(id, companyId)
                .orElseThrow(() -> new ContactNotFoundException(id));

        if (contactRepository.existsByCompanyIdAndEmailAndIdNotAndDeletedFalse(companyId, request.email(), id)) {
            throw new DuplicateContactException(request.email());
        }

        contact.setName(request.name());
        contact.setEmail(request.email());
        contact.setPhone(request.phone());
        contact.setAddress(request.address());
        contact.setNotes(request.notes());

        contact = contactRepository.save(contact);
        return contactMapper.toResponse(contact);
    }

    public void delete(Long id, Long companyId) {
        Contact contact = contactRepository.findByIdAndCompanyIdAndDeletedFalse(id, companyId)
                .orElseThrow(() -> new ContactNotFoundException(id));
        contact.setDeleted(true);
        contactRepository.save(contact);
    }
}
