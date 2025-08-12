package com.example.GestionClinique.dto.ResponseDto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class DossierMedicalResponseDto extends BaseResponseDto {
    private PatientResponseDto patient;
    private String groupeSanguin;
    private String antecedentsMedicaux;
    private String allergies;
    private String DernierTraitement;
    private String observations;
    private List<ConsultationResponseDto> consultations;
}