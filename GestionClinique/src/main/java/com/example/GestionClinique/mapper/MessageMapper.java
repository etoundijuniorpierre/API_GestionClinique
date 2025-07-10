package com.example.GestionClinique.mapper;


import com.example.GestionClinique.dto.RequestDto.MessageRequestDto;
import com.example.GestionClinique.dto.ResponseDto.MessageResponseDto;
import com.example.GestionClinique.model.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = UtilisateurMapper.class)
public interface MessageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "expediteur", ignore = true)
    @Mapping(target = "destinataire", ignore = true)
    Message toEntity(MessageRequestDto dto);

    @Mapping(target = "expediteur", source = "expediteur")
    @Mapping(target = "destinataire", source = "destinataire")
    MessageResponseDto toDto(Message entity);

    List<MessageResponseDto> toDtoList(List<Message> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "expediteur", ignore = true)
    @Mapping(target = "destinataire", ignore = true)
    void updateEntityFromDto(MessageRequestDto dto, @MappingTarget Message entity);
}