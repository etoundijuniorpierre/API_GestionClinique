package com.example.GestionClinique.mapper;

import com.example.GestionClinique.dto.RequestDto.RoleRequestDto;
import com.example.GestionClinique.dto.ResponseDto.RoleResponseDto;
import com.example.GestionClinique.model.entity.Role;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleResponseDto toDto(Role role);
    Role toEntity(RoleRequestDto dto);
    List<RoleResponseDto> toDtoList(List<Role> roles);
}
