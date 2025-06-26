package com.example.GestionClinique.dto;

import com.example.GestionClinique.model.entity.Role;
import com.example.GestionClinique.model.entity.enumElem.RoleType;
import lombok.Builder;
import lombok.Data;



@Data
@Builder
public class RoleDto {
    private Integer id;
    private RoleType roleType;


    public static RoleDto fromEntity(Role role) {
        if (role == null) return null;


        return RoleDto.builder()
                .id(role.getId())
                .roleType(role.getRoleType())
                .build();
    }

    public static Role toEntity(RoleDto roleDto) {
        if (roleDto == null) return null;
        Role role = new Role();
        role.setRoleType(roleDto.getRoleType());
        return role;
    }
}