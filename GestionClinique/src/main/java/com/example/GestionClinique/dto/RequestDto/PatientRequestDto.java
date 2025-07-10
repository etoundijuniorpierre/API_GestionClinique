// PatientDto.java (updated)
package com.example.GestionClinique.dto.RequestDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class PatientRequestDto extends InfoPersonnelRequestDto {
    @Valid
    @NotNull(message = "Les informations du dossier médical sont requises.")
    private DossierMedicalRequestDto dossierMedical;
}