package com.example.GestionClinique.dto.RequestDto;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
public class DossierMedicalRequestDto {

    @NotBlank(message = "Le groupe sanguin est requis.")
    private String groupeSanguin;
    private String antecedentsMedicaux;
    private String allergies;
    private String DernierTraitement;
    private String observations;
    private Long patientId;
}