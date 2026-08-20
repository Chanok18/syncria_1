package com.syncria.module.pet.mapper;

import com.syncria.module.pet.dto.PetRequestDTO;
import com.syncria.module.pet.dto.PetResponseDTO;
import com.syncria.module.pet.entity.Pet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PetMapper {

    PetResponseDTO toResponse(Pet pet);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Pet toEntity(PetRequestDTO dto);
}
