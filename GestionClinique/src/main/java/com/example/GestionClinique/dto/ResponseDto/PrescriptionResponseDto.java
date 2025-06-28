// PrescriptionDto.java (updated)
package com.example.GestionClinique.dto.ResponseDto;

import lombok.*;


@EqualsAndHashCode(callSuper = true)
@Data
public class PrescriptionResponseDto extends BaseResponseDto {
    private String typePrescription;
    private String medicaments;
    private String instructions;
    private String dureePrescription;
    private String quantite;
    private Long consultationId;
    private UtilisateurResponseDto medecinId;
    private PatientResponseDto patientId;
    private DossierMedicalResponseDto dossierMedicalId;


}