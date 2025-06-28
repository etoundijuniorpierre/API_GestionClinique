package com.example.GestionClinique.dto.ResponseDto;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class MessageResponseDto extends BaseResponseDto { // Assuming BaseResponseDto has ID, creationDate, etc.

    private String contenu;

    private boolean lu; // Lombok generates isLu()

    private UtilisateurResponseDto expediteur; // Include full DTO for sender
    private UtilisateurResponseDto destinataire; // Include full DTO for receiver
}