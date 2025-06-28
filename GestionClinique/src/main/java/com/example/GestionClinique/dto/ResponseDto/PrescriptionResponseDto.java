// PrescriptionDto.java (updated)
package com.example.GestionClinique.dto.ResponseDto;

import lombok.*;

import java.time.LocalDate;


@EqualsAndHashCode(callSuper = true)
@Data
@Builder // Add @Builder here too
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionResponseDto extends BaseResponseDto {
    private LocalDate datePrescription; // Added
    private String typePrescription;
    private String medicaments;
    private String instructions;
    private String dureePrescription;
    private Long quantite;
    private Long consultationId;
    private String consultationDescription;
    private Long medecinId;
    private String medecinNomComplet;
    private Long patientId;
    private String patientNomComplet;
    private Long dossierMedicalId;
    private String dossierMedicalReference;
}