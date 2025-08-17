package com.example.GestionClinique.mapper;

import com.example.GestionClinique.dto.RequestDto.RoleRequestDto;
import com.example.GestionClinique.dto.RequestDto.UtilisateurRequestDto;
import com.example.GestionClinique.dto.ResponseDto.RoleResponseDto;
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
    @Mapping(target = "serviceMedicalName", source = "serviceMedical")
    @Mapping(target = "role", source = "role")
    UtilisateurResponseDto toDto(Utilisateur utilisateur);

    List<UtilisateurResponseDto> toDtoList(List<Utilisateur> utilisateurs);

    @Mapping(target = "role", source = "role")
    @Mapping(target = "serviceMedical", source = "serviceMedicalName")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    void updateEntityFromDto(UtilisateurRequestDto dto, @MappingTarget Utilisateur utilisateur);
}
