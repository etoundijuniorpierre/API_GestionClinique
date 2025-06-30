package com.example.GestionClinique.mapper;

import com.example.GestionClinique.dto.RequestDto.UtilisateurRequestDto;
import com.example.GestionClinique.dto.ResponseDto.UtilisateurResponseDto;
import com.example.GestionClinique.model.entity.Role;
import com.example.GestionClinique.model.entity.Utilisateur;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {RoleMapper.class})
public interface UtilisateurMapper {

    @Mapping(target = "role", source = "roleId", qualifiedByName = "mapRoleIdToRole")
    @Mapping(target = "serviceMedical", source = "serviceMedicalName")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    Utilisateur toEntity(UtilisateurRequestDto dto);

    @Mapping(target = "role", source = "role")
    @Mapping(target = "serviceMedicalName", source = "serviceMedical")
    UtilisateurResponseDto toDto(Utilisateur utilisateur);

    List<UtilisateurResponseDto> toDtoList(List<Utilisateur> utilisateurs);

    @Mapping(target = "role", source = "roleId", qualifiedByName = "mapRoleIdToRole")
    @Mapping(target = "serviceMedical", source = "serviceMedicalName")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    void updateEntityFromDto(UtilisateurRequestDto dto, @MappingTarget Utilisateur utilisateur);

    @Named("mapRoleIdToRole")
    default Role mapRoleIdToRole(Long roleId) {
        if (roleId == null) {
            return null;
        }
        Role role = new Role();
        role.setId(roleId);
        return role;
    }
}
