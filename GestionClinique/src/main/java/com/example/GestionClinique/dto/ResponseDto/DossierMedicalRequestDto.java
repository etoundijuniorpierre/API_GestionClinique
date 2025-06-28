package com.example.GestionClinique.dto.ResponseDto;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
public class DossierMedicalRequestDto {

    @NotBlank(message = "Le groupe sanguin est requis.")
    private String groupeSanguin;
    private String antecedentsMedicaux;
    private String allergies;
    private String traitementsEnCours;
    private String observations;
    private Long patientId;
}