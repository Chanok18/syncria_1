package com.syncria.module.contact.mapper;

import com.syncria.module.contact.dto.ContactRequestDTO;
import com.syncria.module.contact.dto.ContactResponseDTO;
import com.syncria.module.contact.entity.Contact;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContactMapper {

    ContactResponseDTO toResponse(Contact contact);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Contact toEntity(ContactRequestDTO dto);
}
