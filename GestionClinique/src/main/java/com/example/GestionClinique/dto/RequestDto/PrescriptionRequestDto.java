// PrescriptionDto.java (updated)
package com.example.GestionClinique.dto.RequestDto;


import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionRequestDto {
    private Long consultantId;

    @NotBlank(message = "Le type de prescription est requis.")
    private String typePrescription;

    @NotBlank(message = "Les médicaments sont requis.")
    private String medicaments;

    @NotBlank(message = "Les instructions sont requises.")
    private String instructions;

    private String dureePrescription; // Can be null if not applicable

    @NotNull(message = "La quantité est requise.")
    @Min(value = 1, message = "La quantité doit être au moins 1.")
    private Long quantite;

//    @NotNull(message = "L'ID du dossier médical est requis.")
//    private Long dossierMedicalId; // Use Long
}