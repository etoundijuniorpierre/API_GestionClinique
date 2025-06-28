package com.example.GestionClinique.dto.ResponseDto;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class HistoriqueActionResponseDto extends BaseResponseDto {
    private String action;
    private UtilisateurResponseDto utilisateur;
}