package com.syncria.module.contact.service;

import com.syncria.module.contact.dto.ContactRequestDTO;
import com.syncria.module.contact.dto.ContactResponseDTO;
import com.syncria.module.contact.entity.Contact;
import com.syncria.module.contact.exception.ContactNotFoundException;
import com.syncria.module.contact.exception.DuplicateContactException;
import com.syncria.module.contact.mapper.ContactMapper;
import com.syncria.module.contact.repository.ContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

    private final ContactMapper contactMapper = Mappers.getMapper(ContactMapper.class);
    private ContactService contactService;

    @BeforeEach
    void setUp() {
        contactService = new ContactService(contactRepository, contactMapper);
    }

    @Test
    void create_ShouldReturnContactResponseDTO() {
        ContactRequestDTO request = new ContactRequestDTO("John Doe", "john@test.com", "123456789", "123 Main St", "Notes");
        Long companyId = 1L;

        when(contactRepository.existsByCompanyIdAndEmailAndDeletedFalse(companyId, request.email())).thenReturn(false);
        when(contactRepository.save(any(Contact.class))).thenAnswer(invocation -> {
            Contact contact = invocation.getArgument(0);
            contact.setId(1L);
            return contact;
        });

        ContactResponseDTO response = contactService.create(request, companyId);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("John Doe");
        assertThat(response.email()).isEqualTo("john@test.com");
        assertThat(response.phone()).isEqualTo("123456789");
        assertThat(response.companyId()).isEqualTo(companyId);
    }

    @Test
    void create_ShouldThrowDuplicateContactException_WhenEmailExists() {
        ContactRequestDTO request = new ContactRequestDTO("John Doe", "duplicate@test.com", "123456789", null, null);
        Long companyId = 1L;

        when(contactRepository.existsByCompanyIdAndEmailAndDeletedFalse(companyId, request.email())).thenReturn(true);

        assertThatThrownBy(() -> contactService.create(request, companyId))
                .isInstanceOf(DuplicateContactException.class)
                .hasMessageContaining("duplicate@test.com");
    }

    @Test
    void findAll_ShouldReturnPaginatedResults() {
        Long companyId = 1L;
        String search = "john";
        PageRequest pageable = PageRequest.of(0, 20);

        Contact contact = Contact.builder()
                .id(1L).companyId(companyId).name("John Doe")
                .email("john@test.com").phone("123456789").build();
        when(contactRepository.findByCompanyIdAndSearch(companyId, search, pageable))
                .thenReturn(new PageImpl<>(List.of(contact)));

        Page<ContactResponseDTO> result = contactService.findAll(companyId, search, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).name()).isEqualTo("John Doe");
    }

    @Test
    void findAll_ShouldReturnEmptyPage_WhenNoMatches() {
        when(contactRepository.findByCompanyIdAndSearch(1L, "nonexistent", PageRequest.of(0, 20)))
                .thenReturn(Page.empty());

        Page<ContactResponseDTO> result = contactService.findAll(1L, "nonexistent", PageRequest.of(0, 20));

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void findById_ShouldReturnContact() {
        Long companyId = 1L;
        Contact contact = Contact.builder()
                .id(1L).companyId(companyId).name("John Doe")
                .email("john@test.com").build();

        when(contactRepository.findByIdAndCompanyIdAndDeletedFalse(1L, companyId))
                .thenReturn(Optional.of(contact));

        ContactResponseDTO response = contactService.findById(1L, companyId);

        assertThat(response.name()).isEqualTo("John Doe");
        assertThat(response.email()).isEqualTo("john@test.com");
    }

    @Test
    void findById_ShouldThrowContactNotFoundException_WhenNotFound() {
        when(contactRepository.findByIdAndCompanyIdAndDeletedFalse(99L, 1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> contactService.findById(99L, 1L))
                .isInstanceOf(ContactNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void update_ShouldReturnUpdatedContact() {
        Long companyId = 1L;
        Contact existing = Contact.builder()
                .id(1L).companyId(companyId).name("Old Name")
                .email("old@test.com").phone("111").build();
        ContactRequestDTO request = new ContactRequestDTO("New Name", "new@test.com", "222", "New Address", "New notes");

        when(contactRepository.findByIdAndCompanyIdAndDeletedFalse(1L, companyId))
                .thenReturn(Optional.of(existing));
        when(contactRepository.existsByCompanyIdAndEmailAndIdNotAndDeletedFalse(companyId, "new@test.com", 1L))
                .thenReturn(false);
        when(contactRepository.save(any(Contact.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ContactResponseDTO response = contactService.update(1L, request, companyId);

        assertThat(response.name()).isEqualTo("New Name");
        assertThat(response.email()).isEqualTo("new@test.com");
        assertThat(response.phone()).isEqualTo("222");
        assertThat(response.address()).isEqualTo("New Address");
    }

    @Test
    void update_ShouldThrowDuplicateContactException_WhenEmailTaken() {
        Long companyId = 1L;
        Contact existing = Contact.builder()
                .id(1L).companyId(companyId).name("Old Name")
                .email("old@test.com").build();
        ContactRequestDTO request = new ContactRequestDTO("New Name", "taken@test.com", null, null, null);

        when(contactRepository.findByIdAndCompanyIdAndDeletedFalse(1L, companyId))
                .thenReturn(Optional.of(existing));
        when(contactRepository.existsByCompanyIdAndEmailAndIdNotAndDeletedFalse(companyId, "taken@test.com", 1L))
                .thenReturn(true);

        assertThatThrownBy(() -> contactService.update(1L, request, companyId))
                .isInstanceOf(DuplicateContactException.class)
                .hasMessageContaining("taken@test.com");
    }

    @Test
    void delete_ShouldSetDeletedTrue() {
        Long companyId = 1L;
        Contact contact = Contact.builder()
                .id(1L).companyId(companyId).name("John Doe")
                .email("john@test.com").deleted(false).build();

        when(contactRepository.findByIdAndCompanyIdAndDeletedFalse(1L, companyId))
                .thenReturn(Optional.of(contact));

        contactService.delete(1L, companyId);

        assertThat(contact.getDeleted()).isTrue();
        verify(contactRepository).save(contact);
    }

    @Test
    void delete_ShouldThrowContactNotFoundException_WhenNotFound() {
        when(contactRepository.findByIdAndCompanyIdAndDeletedFalse(99L, 1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> contactService.delete(99L, 1L))
                .isInstanceOf(ContactNotFoundException.class)
                .hasMessageContaining("99");
    }
}
