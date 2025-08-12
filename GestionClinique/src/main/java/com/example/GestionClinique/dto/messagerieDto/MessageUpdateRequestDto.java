package com.example.GestionClinique.dto.messagerieDto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageUpdateRequestDto {
    @NotNull
    private Long id;

    private String contenu;

    private Boolean lu;

    private Long destinataireId; // optionnel, pour modifier destinataire si besoin

    private Long groupeId; // optionnel, pour modifier groupe si besoin
}

