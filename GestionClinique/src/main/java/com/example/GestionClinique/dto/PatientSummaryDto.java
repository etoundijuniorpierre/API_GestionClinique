package com.example.GestionClinique.dto;

import com.example.GestionClinique.model.entity.Patient;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PatientSummaryDto {
    private Integer id;
    private InfoPersonnelDto infoPersonnel; // On peut réutiliser InfoPersonnelDto ici

    public static PatientSummaryDto fromEntity(Patient patient) {
        if (patient == null) {
            return null;
        }
        return PatientSummaryDto.builder()
                .id(patient.getId())
                .infoPersonnel(InfoPersonnelDto.fromEntity(patient.getInfoPersonnel()))
                .build();
    }
}