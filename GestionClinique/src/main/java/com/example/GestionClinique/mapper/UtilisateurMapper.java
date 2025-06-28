package com.example.GestionClinique.mapper;

import com.example.GestionClinique.dto.RequestDto.UtilisateurRequestDto;
import com.example.GestionClinique.dto.ResponseDto.UtilisateurResponseDto;
import com.example.GestionClinique.model.entity.Role;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.repository.RoleRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UtilisateurMapper {


    RoleRepository roleRepository = null;


//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "actif", defaultValue = "true")
//    @Mapping(target = "rendezVous", ignore = true)
//    @Mapping(target = "consultations", ignore = true)
//    @Mapping(target = "prescriptions", ignore = true)
//    @Mapping(target = "messagesEnvoyes", ignore = true)
//    @Mapping(target = "messagesRecus", ignore = true)
//    @Mapping(target = "historiqueActions", ignore = true)
    @Mapping(target = "role", source = "roleId", qualifiedByName = "mapRoleIdToRole")
    Utilisateur toEntity(UtilisateurRequestDto dto);

    @Mapping(target = "role", source = "role") // Simple mapping for Role to RoleResponseDto
    UtilisateurResponseDto toDto(Utilisateur utilisateur);


    List<UtilisateurResponseDto> toDtoList(List<Utilisateur> utilisateurs);

//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "email", ignore = true)
//    @Mapping(target = "role", source = "roleId", qualifiedByName = "mapRoleIdToRole")
//    @Mapping(target = "rendezVous", ignore = true)
//    @Mapping(target = "consultations", ignore = true)
//    @Mapping(target = "prescriptions", ignore = true)
//    @Mapping(target = "messagesEnvoyes", ignore = true)
//    @Mapping(target = "messagesRecus", ignore = true)
//    @Mapping(target = "historiqueActions", ignore = true)
    void updateEntityFromDto(UtilisateurRequestDto utilisateurRequestDto, @MappingTarget Utilisateur utilisateur);


    @Named("mapRoleIdToRole")
    default Role mapRoleIdToRole(Long roleId) {
        if (roleId == null) {
            return null;
        }

        return roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role with ID " + roleId + " not found."));
    }
}
