package com.example.GestionClinique.mapper;

import com.example.GestionClinique.dto.RequestDto.UtilisateurRequestDto;
import com.example.GestionClinique.dto.ResponseDto.UtilisateurResponseDto;
import com.example.GestionClinique.model.entity.Role;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.RoleType;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {RoleMapper.class})
public interface UtilisateurMapper {

    @Mapping(target = "photoProfil", ignore = true)
    @Mapping(target = "role", source = "role", qualifiedByName = "mapRoleIdToRole")
    @Mapping(target = "serviceMedical", source = "serviceMedicalName")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "lastLoginDate", ignore = true)
    @Mapping(target = "lastLogoutDate", ignore = true)
    @Mapping(target = "statusConnect", ignore = true)
    Utilisateur toEntity(UtilisateurRequestDto dto);



    @Mapping(target = "lastLoginDate", source = "lastLoginDate")
    @Mapping(target = "lastLogoutDate", source = "lastLogoutDate")
    @Mapping(target = "statusConnect", source = "statusConnect")
    UtilisateurResponseDto toDto(Utilisateur utilisateur);


    List<UtilisateurResponseDto> toDtoList(List<Utilisateur> utilisateurs);

    @Mapping(target = "role", source = "role", qualifiedByName = "mapRoleIdToRole")
    @Mapping(target = "serviceMedical", source = "serviceMedicalName")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    void updateEntityFromDto(UtilisateurRequestDto dto, @MappingTarget Utilisateur utilisateur);

    @Named("mapRoleIdToRole")
    default Role mapRoleIdToRole(String roleName) {
        if (roleName == null || roleName.isBlank()) {
            return null;
        }
        Role role = new Role();
        role.setRoleType(RoleType.valueOf(roleName.toUpperCase())); // Assure que le nom correspond à un enum

        return role;
    }


}

