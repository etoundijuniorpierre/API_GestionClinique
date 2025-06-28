package com.example.GestionClinique.dto;

import com.example.GestionClinique.model.entity.DossierMedical;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime; // Import for creationDate

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DossierMedicalSummaryDto {
    private Integer id;
    private String observations; // A key summary field for a medical record
    private LocalDateTime creationDate; // From EntityAbstracte, useful in summary
    private String groupeSanguin; // Included if deemed important for summary view

    // Use an ID for the patient to avoid a circular dependency with PatientDto
    private Integer patientId;

    public static DossierMedicalSummaryDto fromEntity(DossierMedical dossierMedical) {
        if (dossierMedical == null) {
            return null;
        }
        return DossierMedicalSummaryDto.builder()
                .id(dossierMedical.getId())
                .observations(dossierMedical.getObservations())
                .creationDate(dossierMedical.getCreationDate()) // Map creationDate
                .groupeSanguin(dossierMedical.getGroupeSanguin()) // Map groupeSanguin
                .patientId(dossierMedical.getPatient() != null ? dossierMedical.getPatient().getId() : null)
                .build();
    }

    // The toEntity method for a summary DTO is generally not needed for write operations.
    // Its primary purpose is to provide a simplified view for read operations.
    // If you do need one, ensure it's very basic and handles relationships in the service.
    public static DossierMedical toEntity(DossierMedicalSummaryDto dto) {
        if (dto == null) return null;
        DossierMedical entity = new DossierMedical();
        entity.setId(dto.getId());
        entity.setObservations(dto.getObservations());
        entity.setGroupeSanguin(dto.getGroupeSanguin()); // Set groupeSanguin if provided
        // creationDate is automatically set by @CreationTimestamp on entity creation
        // Related patient entity would be set in the service layer using dto.getPatientId()
        return entity;
    }
}