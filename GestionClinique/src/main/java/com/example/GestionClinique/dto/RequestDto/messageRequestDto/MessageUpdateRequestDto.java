package com.example.GestionClinique.dto.RequestDto.messageRequestDto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageUpdateRequestDto {
    @NotNull
    private Long id;

    private String contenu;

    private Boolean lu;

    private Long groupeId;
}

