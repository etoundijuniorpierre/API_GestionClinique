package com.example.GestionClinique.dto.RequestDto;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageRequestDto {
    @NotNull
    private String contenu;

    private boolean lu;


}