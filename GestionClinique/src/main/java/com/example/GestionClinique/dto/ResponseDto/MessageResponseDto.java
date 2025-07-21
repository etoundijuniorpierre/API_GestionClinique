package com.example.GestionClinique.dto.ResponseDto;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class MessageResponseDto extends BaseResponseDto { // Assuming BaseResponseDto has ID, creationDate, etc.

    private String contenu;

    private boolean lu;

    private UtilisateurResponseDto expediteur;
    private UtilisateurResponseDto destinataire;
}