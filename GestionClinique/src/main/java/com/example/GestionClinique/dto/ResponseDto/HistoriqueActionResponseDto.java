package com.example.GestionClinique.dto.ResponseDto;

import lombok.*;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class HistoriqueActionResponseDto extends BaseResponseDto {
    private LocalDate date;
    private String action;
    private UtilisateurResponseDto utilisateur;
}
