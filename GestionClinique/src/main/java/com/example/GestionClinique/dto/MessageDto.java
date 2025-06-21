package com.example.GestionClinique.dto;

import com.example.GestionClinique.model.entity.Message;
import lombok.*;

import java.time.LocalDateTime;


@Data
@Builder
public class MessageDto {
    private Integer id;
    private String contenu;
    private LocalDateTime dateEnvoi;
    private boolean lu;
    private UtilisateurDto expediteur;
    private UtilisateurDto destinataire;

    public static MessageDto fromEnity(Message message) { // fromEnity -> fromEntity (typo)
        if(message == null) return null;

        return MessageDto.builder()
                .id(message.getId())
                .contenu(message.getContenu())
                .dateEnvoi(message.getDateEnvoi())
                .lu(message.isLu())
                .expediteur(UtilisateurDto.fromEntity(message.getExpediteur()))
                .destinataire(UtilisateurDto.fromEntity(message.getDestinataire()))
                .build();
    }

    public static Message toEntity(MessageDto messageDto ) {
        if(messageDto == null) return null;

        Message message = new Message();
        // L'ID n'est généralement pas défini ici pour la création d'une nouvelle entité.
        message.setContenu(messageDto.getContenu());
        message.setDateEnvoi(messageDto.getDateEnvoi());
        message.setLu(messageDto.isLu());
        message.setExpediteur(UtilisateurDto.toEntity(messageDto.getExpediteur()));
        message.setDestinataire(UtilisateurDto.toEntity(messageDto.getDestinataire()));
        return message;
    }
}
