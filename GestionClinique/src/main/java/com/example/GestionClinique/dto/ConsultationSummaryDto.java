package com.example.GestionClinique.dto;

import com.example.GestionClinique.model.entity.Consultation;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConsultationSummaryDto {
    private Integer id;
    private String motifs;
    private String diagnostic;
    // Do NOT add references to DossierMedicalDto, UtilisateurDto, RendezVousDto,
    // PrescriptionDto, FactureDto to avoid recursion. Use IDs if necessary.
    private Integer rendezVousId; // Example: if RendezVous needs to refer back
    private Integer patientId; // Derived from DossierMedical -> Patient

    public static ConsultationSummaryDto fromEntity(Consultation consultation) {
        if (consultation == null) {
            return null;
        }
        return ConsultationSummaryDto.builder()
                .id(consultation.getId())
                .motifs(consultation.getMotifs())
                .diagnostic(consultation.getDiagnostic())
                .rendezVousId(consultation.getRendezVous() != null ? consultation.getRendezVous().getId() : null)
                .patientId(consultation.getDossierMedical() != null && consultation.getDossierMedical().getPatient() != null ? consultation.getDossierMedical().getPatient().getId() : null)
                .build();
    }

    public static Consultation toEntity(ConsultationSummaryDto dto) {
        if (dto == null) return null;
        Consultation entity = new Consultation();
        entity.setId(dto.getId());
        entity.setMotifs(dto.getMotifs());
        entity.setDiagnostic(dto.getDiagnostic());
        // Relationships (rendezVous, dossierMedical, medecin) would be set in service
        // based on IDs, not directly from a summary DTO.
        return entity;
    }
}