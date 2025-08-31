package com.example.GestionClinique.mapper;

import com.example.GestionClinique.dto.ResponseDto.messageResponseDto.HistoriqueMessageResponseDto;
import com.example.GestionClinique.model.entity.HistoriqueMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UtilisateurMapper.class})
public interface HistoriqueMessageMapper {
    @Mapping(target = "action", expression = "java(entity.getAction().name())")
    HistoriqueMessageResponseDto toDto(HistoriqueMessage entity);
}
