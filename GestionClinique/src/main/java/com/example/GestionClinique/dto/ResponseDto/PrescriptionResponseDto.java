// PrescriptionDto.java (updated)
package com.example.GestionClinique.dto.ResponseDto;

import lombok.*;

import java.time.LocalDate;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionResponseDto extends BaseResponseDto {
    private Long medecinId;
    private String medecinNomComplet;
    private Long patientId;
    private String patientNomComplet;
    private String typePrescription;
    private String medicaments;
    private String instructions;
    private String dureePrescription;
    private Long quantite;
    private String motifConsultation;
    private Long consultationId;
//    private Long dossierMedicalId;
//    private String dossierMedicalReference;
}
