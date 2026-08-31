package com.syncria.module.pet.service;

import com.syncria.module.contact.entity.Contact;
import com.syncria.module.contact.repository.ContactRepository;
import com.syncria.module.pet.dto.PetRequestDTO;
import com.syncria.module.pet.dto.PetResponseDTO;
import com.syncria.module.pet.entity.Pet;
import com.syncria.module.pet.exception.PetNotFoundException;
import com.syncria.module.pet.mapper.PetMapper;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private ContactRepository contactRepository;

    private final PetMapper petMapper = Mappers.getMapper(PetMapper.class);
    private PetService petService;

    @BeforeEach
    void setUp() {
        petService = new PetService(petRepository, petMapper, contactRepository);
    }

    private Contact buildContact(Long id, Long companyId) {
        return Contact.builder()
                .id(id)
                .companyId(companyId)
                .name("Owner Name")
                .email("owner@test.com")
                .build();
    }

    private Pet buildPet(Long id, Long companyId, Long contactId) {
        return Pet.builder()
                .id(id)
                .companyId(companyId)
                .contactId(contactId)
                .name("Buddy")
                .species("Dog")
                .breed("Golden Retriever")
                .gender("Male")
                .build();
    }

    @Test
    void create_ShouldReturnPetResponseDTO() {
        Long companyId = 1L;
        Long contactId = 10L;
        PetRequestDTO request = new PetRequestDTO(contactId, "Buddy", "Dog", "Golden Retriever", null, "Male", null);

        when(contactRepository.findByIdAndCompanyIdAndDeletedFalse(contactId, companyId))
                .thenReturn(Optional.of(buildContact(contactId, companyId)));
        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> {
            Pet pet = invocation.getArgument(0);
            pet.setId(1L);
            return pet;
        });

        PetResponseDTO response = petService.create(request, companyId);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Buddy");
        assertThat(response.species()).isEqualTo("Dog");
        assertThat(response.companyId()).isEqualTo(companyId);
        assertThat(response.contactId()).isEqualTo(contactId);
    }

    @Test
    void create_ShouldThrowPetNotFoundException_WhenContactNotFound() {
        PetRequestDTO request = new PetRequestDTO(99L, "Buddy", "Dog", null, null, null, null);

        when(contactRepository.findByIdAndCompanyIdAndDeletedFalse(99L, 1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> petService.create(request, 1L))
                .isInstanceOf(PetNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void findAll_ShouldUseDerivedQuery_WhenSearchIsNull() {
        Long companyId = 1L;
        PageRequest pageable = PageRequest.of(0, 20);

        Pet pet = buildPet(1L, companyId, 10L);
        when(petRepository.findByCompanyIdAndDeletedFalse(companyId, pageable))
                .thenReturn(new PageImpl<>(List.of(pet)));

        Page<PetResponseDTO> result = petService.findAll(companyId, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).name()).isEqualTo("Buddy");
        verify(petRepository).findByCompanyIdAndDeletedFalse(companyId, pageable);
        verify(petRepository, never()).searchByCompanyId(any(), any(), any());
    }

    @Test
    void findAll_ShouldUseSearchQuery_WhenSearchProvided() {
        Long companyId = 1L;
        PageRequest pageable = PageRequest.of(0, 20);

        Pet pet = buildPet(1L, companyId, 10L);
        when(petRepository.searchByCompanyId(companyId, "buddy", pageable))
                .thenReturn(new PageImpl<>(List.of(pet)));

        Page<PetResponseDTO> result = petService.findAll(companyId, "buddy", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).name()).isEqualTo("Buddy");
        verify(petRepository).searchByCompanyId(companyId, "buddy", pageable);
        verify(petRepository, never()).findByCompanyIdAndDeletedFalse(any(), any());
    }

    @Test
    void findAll_ShouldReturnEmptyPage_WhenNoMatches() {
        when(petRepository.findByCompanyIdAndDeletedFalse(1L, PageRequest.of(0, 20)))
                .thenReturn(Page.empty());

        Page<PetResponseDTO> result = petService.findAll(1L, null, PageRequest.of(0, 20));

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void findById_ShouldReturnPet() {
        Long companyId = 1L;
        Pet pet = buildPet(1L, companyId, 10L);

        when(petRepository.findByIdAndCompanyIdAndDeletedFalse(1L, companyId))
                .thenReturn(Optional.of(pet));

        PetResponseDTO response = petService.findById(1L, companyId);

        assertThat(response.name()).isEqualTo("Buddy");
        assertThat(response.species()).isEqualTo("Dog");
    }

    @Test
    void findById_ShouldThrowPetNotFoundException_WhenNotFound() {
        when(petRepository.findByIdAndCompanyIdAndDeletedFalse(99L, 1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> petService.findById(99L, 1L))
                .isInstanceOf(PetNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void findByContactId_ShouldReturnPetsForContact() {
        Long companyId = 1L;
        Long contactId = 10L;
        Contact contact = buildContact(contactId, companyId);
        Pet pet1 = buildPet(1L, companyId, contactId);
        Pet pet2 = buildPet(2L, companyId, contactId);

        when(contactRepository.findByIdAndCompanyIdAndDeletedFalse(contactId, companyId))
                .thenReturn(Optional.of(contact));
        when(petRepository.findByContactIdAndCompanyIdAndDeletedFalse(contactId, companyId))
                .thenReturn(List.of(pet1, pet2));

        List<PetResponseDTO> result = petService.findByContactId(contactId, companyId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Buddy");
    }

    @Test
    void findByContactId_ShouldThrowPetNotFoundException_WhenContactNotFound() {
        when(contactRepository.findByIdAndCompanyIdAndDeletedFalse(99L, 1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> petService.findByContactId(99L, 1L))
                .isInstanceOf(PetNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void update_ShouldReturnUpdatedPet() {
        Long companyId = 1L;
        Long contactId = 10L;
        Pet existing = buildPet(1L, companyId, contactId);
        PetRequestDTO request = new PetRequestDTO(contactId, "Max", "Dog", "Labrador", null, "Male", "Updated notes");

        when(petRepository.findByIdAndCompanyIdAndDeletedFalse(1L, companyId))
                .thenReturn(Optional.of(existing));
        when(contactRepository.findByIdAndCompanyIdAndDeletedFalse(contactId, companyId))
                .thenReturn(Optional.of(buildContact(contactId, companyId)));
        when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PetResponseDTO response = petService.update(1L, request, companyId);

        assertThat(response.name()).isEqualTo("Max");
        assertThat(response.species()).isEqualTo("Dog");
        assertThat(response.breed()).isEqualTo("Labrador");
    }

    @Test
    void update_ShouldThrowPetNotFoundException_WhenNotFound() {
        when(petRepository.findByIdAndCompanyIdAndDeletedFalse(99L, 1L))
                .thenReturn(Optional.empty());

        PetRequestDTO request = new PetRequestDTO(10L, "Max", "Dog", null, null, null, null);

        assertThatThrownBy(() -> petService.update(99L, request, 1L))
                .isInstanceOf(PetNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void delete_ShouldSetDeletedTrue() {
        Long companyId = 1L;
        Pet pet = buildPet(1L, companyId, 10L);
        pet.setDeleted(false);

        when(petRepository.findByIdAndCompanyIdAndDeletedFalse(1L, companyId))
                .thenReturn(Optional.of(pet));

        petService.delete(1L, companyId);

        assertThat(pet.getDeleted()).isTrue();
        verify(petRepository).save(pet);
    }

    @Test
    void delete_ShouldThrowPetNotFoundException_WhenNotFound() {
        when(petRepository.findByIdAndCompanyIdAndDeletedFalse(99L, 1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> petService.delete(99L, 1L))
                .isInstanceOf(PetNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void countByCompany_ShouldReturnCount() {
        when(petRepository.countByCompanyIdAndDeletedFalse(1L)).thenReturn(5L);

        long count = petService.countByCompany(1L);

        assertThat(count).isEqualTo(5L);
    }
}
