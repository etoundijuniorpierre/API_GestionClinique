package com.example.GestionClinique.mapper;


import com.example.GestionClinique.dto.RequestDto.messageRequestDto.MessageRequestDto;
import com.example.GestionClinique.dto.ResponseDto.messageResponseDto.MessageResponseDto;
import com.example.GestionClinique.model.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", uses = {UtilisateurMapper.class, GroupeMapper.class})
public interface MessageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "modificationDate", ignore = true)
    @Mapping(target = "expediteur", ignore = true)
    @Mapping(target = "conversation", ignore = true)
    Message toEntity(MessageRequestDto dto);

    @Mapping(target = "expediteur", source = "expediteur")
    @Mapping(target = "conversationId", source = "conversation.id")
    MessageResponseDto toDto(Message entity);

    void updateEntityFromDto(MessageRequestDto dto, @MappingTarget Message entity);
}
