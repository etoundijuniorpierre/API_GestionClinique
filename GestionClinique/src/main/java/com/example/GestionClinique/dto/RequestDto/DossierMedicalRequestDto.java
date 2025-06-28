package com.example.GestionClinique.dto.RequestDto;

import lombok.*;

@Data
public class DossierMedicalRequestDto {
    private String groupeSanguin;
    private String antecedentsMedicaux;
    private String allergies;
    private String traitementsEnCours;
    private String observations;
    private Long patientId;
    private Long consultationsId;
    private Long prescriptionsId;

}