package com.example.GestionClinique.dto;

import com.example.GestionClinique.model.entity.enumElem.RoleType;
import lombok.*;

import com.example.GestionClinique.model.entity.Role;


@Data
@Builder
public class RoleDto {
    private Integer id;
    private RoleType type;

    public static RoleDto fromEntity(Role role) {
        if (role == null) return null;
        return RoleDto.builder()
                .id(role.getId())
                .type(role.getRoleType())
                .build();
    }

    public static Role toEntity(RoleDto dto) {
        if (dto == null) return null;
        Role entity = new Role();
        entity.setId(dto.getId());
        entity.setRoleType(dto.getType());
        return entity;
    }
}
