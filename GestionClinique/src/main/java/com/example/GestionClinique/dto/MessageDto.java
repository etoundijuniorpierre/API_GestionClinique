package com.example.GestionClinique.dto;

import com.example.GestionClinique.model.entity.Message;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {
    private Integer id;
    private String contenu;
    private LocalDateTime dateEnvoi;
    private boolean lu; // This is the field causing the issue

    // --- ADD THIS EXPLICIT GETTER FOR THE DTO ---
    public boolean getLu() {
        return this.lu;
    }


    private UtilisateurSummaryDto expediteurSummary;
    private UtilisateurSummaryDto destinataireSummary;

    public static MessageDto fromEntity(Message message) {
        if(message == null) return null;

        return MessageDto.builder()
                .id(message.getId())
                .contenu(message.getContenu())
                .dateEnvoi(message.getDateEnvoi())
                .lu(message.getLu()) // Use getLu() for the entity
                .expediteurSummary(UtilisateurSummaryDto.fromEntity(message.getExpediteur()))
                .destinataireSummary(UtilisateurSummaryDto.fromEntity(message.getDestinataire()))
                .build();
    }

    public static Message toEntity(MessageDto messageDto) {
        if(messageDto == null) return null;

        Message message = new Message();
        message.setId(messageDto.getId());
        message.setContenu(messageDto.getContenu());
        message.setDateEnvoi(messageDto.getDateEnvoi());
        message.setLu(messageDto.getLu()); // Use getLu() for the DTO
        return message;
    }
}