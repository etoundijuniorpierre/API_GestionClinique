// PrescriptionDto.java (updated)
package com.example.GestionClinique.dto.RequestDto;


import lombok.*;

import java.time.LocalDate;


@Data
public class PrescriptionRequestDto {
    private LocalDate datePrescription;
    private String typePrescription;
    private String medicaments;
    private String instructions;
    private String dureePrescription;
    private Integer quantite;
    private Long consultationId;
    private Long medecinId;
    private Long patientId;
    private Long dossierMedicalId;
}