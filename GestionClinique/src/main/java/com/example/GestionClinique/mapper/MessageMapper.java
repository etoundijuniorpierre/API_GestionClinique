package com.example.GestionClinique.mapper;


import com.example.GestionClinique.dto.messagerieDto.MessageRequestDto;
import com.example.GestionClinique.dto.messagerieDto.MessageResponseDto;
import com.example.GestionClinique.model.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring", uses = {UtilisateurMapper.class, GroupeMapper.class})
public interface MessageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "expediteur", ignore = true)
    @Mapping(target = "destinataire", ignore = true)
    @Mapping(target = "groupe", ignore = true)
    Message toEntity(MessageRequestDto dto);

    @Mapping(target = "expediteur", source = "expediteur")
    @Mapping(target = "destinataire", source = "destinataire")
    @Mapping(target = "groupe", source = "groupe")
    MessageResponseDto toDto(Message entity);

    void updateEntityFromDto(MessageRequestDto dto, @MappingTarget Message entity);
}

