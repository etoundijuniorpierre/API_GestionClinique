package com.example.GestionClinique.mapper;

import com.example.GestionClinique.dto.ResponseDto.RoleResponseDto;
import com.example.GestionClinique.model.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleResponseDto toDto(Role role);
}
