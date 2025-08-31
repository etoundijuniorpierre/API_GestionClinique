package com.example.GestionClinique.mapper;

import com.example.GestionClinique.dto.RequestDto.messageRequestDto.ConversationRequestDto;
import com.example.GestionClinique.dto.ResponseDto.UtilisateurResponseDto;
import com.example.GestionClinique.dto.ResponseDto.messageResponseDto.ConversationResponseDto;
import com.example.GestionClinique.model.entity.Conversation;
import com.example.GestionClinique.model.entity.ConversationParticipant;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.repository.UtilisateurRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UtilisateurMapper.class, GroupeMapper.class})
public abstract class ConversationMapper {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private UtilisateurMapper utilisateurMapper;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "participants", source = "participantIds", qualifiedByName = "mapParticipantIdsToParticipants")
    @Mapping(target = "groupe", ignore = true)
    @Mapping(target = "lastMessageAt", ignore = true)
    @Mapping(target = "lastMessageSender", ignore = true)
    public abstract Conversation toEntity(ConversationRequestDto dto);

    @Mapping(target = "typeConversation", source = "typeConversation")
    @Mapping(target = "participants", source = "participants")
    @Mapping(target = "unreadCount", ignore = true)
    @Mapping(target = "lastMessageAt", source = "lastMessageAt")
    public abstract ConversationResponseDto toDto(Conversation entity);

    public abstract List<ConversationResponseDto> toDtoList(List<Conversation> entities);


    @Named("mapParticipantIdsToParticipants")
    public List<ConversationParticipant> mapParticipantIdsToParticipants(List<Long> participantIds) {
        if (participantIds == null || participantIds.isEmpty()) {
            return List.of();
        }
        return participantIds.stream()
                .map(id -> {
                    Utilisateur utilisateur = utilisateurRepository.findById(id)
                            .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec ID: " + id));
                    ConversationParticipant cp = new ConversationParticipant();
                    cp.setUtilisateur(utilisateur);
                    return cp;
                })
                .collect(Collectors.toList());
    }

    protected UtilisateurResponseDto map(ConversationParticipant participant) {
        if (participant == null) return null;
        return utilisateurMapper.toDto(participant.getUtilisateur());
    }
}


