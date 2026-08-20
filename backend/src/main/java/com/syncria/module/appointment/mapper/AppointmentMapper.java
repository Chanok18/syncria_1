package com.syncria.module.appointment.mapper;

import com.syncria.module.appointment.dto.AppointmentRequestDTO;
import com.syncria.module.appointment.dto.AppointmentResponseDTO;
import com.syncria.module.appointment.entity.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(target = "petName", ignore = true)
    @Mapping(target = "contactName", ignore = true)
    AppointmentResponseDTO toResponse(Appointment appointment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "status", ignore = true)
    Appointment toEntity(AppointmentRequestDTO dto);
}
