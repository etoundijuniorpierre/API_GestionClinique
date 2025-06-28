package com.example.GestionClinique.dto.RequestDto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data

public class MessageRequestDto {
    @NotBlank(message = "Le contenu du message ne peut pas être vide.")
    private String contenu;

    // 'lu' typically defaults to false on creation and is updated later.
    // So, it might not be required for initial creation requests, or can be optional.
    private boolean lu; // Default value for boolean is false, so it's fine if not explicitly set

    @NotNull(message = "L'ID de l'expéditeur est requis.")
    private Long expediteurId; // Use Long for IDs for consistency

    @NotNull(message = "L'ID du destinataire est requis.")
    private Long destinataireId; // Use Long for IDs for consistency
}