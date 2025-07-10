package com.example.GestionClinique.dto.ResponseDto;

import lombok.*;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class HistoriqueActionResponseDto extends BaseResponseDto { // Assuming BaseResponseDto has ID, creationDate, etc.
    private LocalDate date; // Added date to response DTO
    private String action;
    private UtilisateurResponseDto utilisateur; // Nested DTO for user details
}