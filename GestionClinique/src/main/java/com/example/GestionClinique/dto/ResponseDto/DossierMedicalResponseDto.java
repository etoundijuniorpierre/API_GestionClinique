package com.example.GestionClinique.dto.ResponseDto;

import com.example.GestionClinique.dto.dtoConnexion.PrescriptionResponseDto;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class DossierMedicalResponseDto extends BaseResponseDto {
    private String groupeSanguin;
    private String antecedentsMedicaux;
    private String allergies;
    private String traitementsEnCours;
    private String observations;
    private PatientResponseDto patientId;
    private ConsultationResponseDto consultationsId;
    private PrescriptionResponseDto prescriptionsId;
}