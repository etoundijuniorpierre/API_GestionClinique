package com.example.GestionClinique.dto.RequestDto.messageRequestDto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageRequestDto {
    @NotBlank(message = "Le contenu ne peut pas être vide.")
    private String contenu;

    private boolean lu;

    @NotNull(message = "ID de l'expéditeur requis")
    private Long expediteurId;

    private Long groupeId;

    private Long conversationId;
}

