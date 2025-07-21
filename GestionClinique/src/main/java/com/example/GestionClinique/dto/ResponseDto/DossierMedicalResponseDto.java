package com.example.GestionClinique.dto.ResponseDto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class DossierMedicalResponseDto extends BaseResponseDto {
    private String patientNomComplet;
    private String patientTelephone;
    private LocalDate patientDateNaissance;
    private String patientGenre;
    private String groupeSanguin;
    private String antecedentsMedicaux;
    private String allergies;
    private String traitementsEnCours;
    private String observations;
    private List<ConsultationResponseDto> consultations;
}