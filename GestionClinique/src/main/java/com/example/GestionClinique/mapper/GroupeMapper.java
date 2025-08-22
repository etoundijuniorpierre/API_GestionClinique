package com.example.GestionClinique.mapper;

import com.example.GestionClinique.dto.RequestDto.messageRequestDto.GroupeRequestDto;
import com.example.GestionClinique.dto.ResponseDto.messageResponseDto.GroupeResponseDto;
import com.example.GestionClinique.model.entity.Groupe;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = UtilisateurMapper.class)
public interface GroupeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "membres", ignore = true)
    @Mapping(target = "createur", ignore = true)
    Groupe toEntity(GroupeRequestDto dto);

    @Mapping(target = "createur", source = "createur")
    GroupeResponseDto toDto(Groupe entity);

    List<GroupeResponseDto> toDtoList(List<Groupe> entities);
}


