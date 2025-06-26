// PatientDto.java (updated)
package com.example.GestionClinique.dto;

import com.example.GestionClinique.model.entity.Patient;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PatientDto {
    private Integer id;
    private InfoPersonnelDto infoPersonnel;
    // ... other fields like rendezVousSummaries, prescriptionSummaries ...

    // CHANGE: Use FactureSummaryDto for the list of invoices
    private List<FactureSummaryDto> factureSummaries;

    public static PatientDto fromEntity(Patient patient) {
        if (patient == null) return null;

        // ... mapping for rendezVousSummaries, prescriptionSummaries ...

        // Map Factures to FactureSummaryDto
        List<FactureSummaryDto> factureSummaries = (patient.getFactures() != null) ?
                patient.getFactures().stream().map(FactureSummaryDto::fromEntity).collect(Collectors.toList()) : null;

        return PatientDto.builder()
                .id(patient.getId())
                .infoPersonnel(InfoPersonnelDto.fromEntity(patient.getInfoPersonnel()))
                // .rendezVousSummaries(...) // Add these back if you have them
                .factureSummaries(factureSummaries) // Use the new summary list
                // .prescriptionSummaries(...) // Add these back if you have them
                .build();
    }

    public static Patient toEntity(PatientDto patientDto) {
        if (patientDto == null) return null;

        Patient patient = new Patient();
        patient.setId(patientDto.getId());

        if (patientDto.getInfoPersonnel() != null) {
            patient.setInfoPersonnel(InfoPersonnelDto.toEntity(patientDto.getInfoPersonnel()));
        }

        return patient;
    }
}