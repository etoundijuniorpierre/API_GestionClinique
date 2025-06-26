// FactureSummaryDto.java
package com.example.GestionClinique.dto;

import com.example.GestionClinique.model.entity.Facture;
import com.example.GestionClinique.model.entity.enumElem.StatutPaiement;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal; // Use BigDecimal for monetary values to avoid precision issues
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FactureSummaryDto {
    private Integer id;
    private Float montant; // Changed from Float to BigDecimal for financial accuracy
    private LocalDate dateEmission;
    private StatutPaiement statutPaiement;

    // Use IDs instead of full DTOs to break potential cycles
    private Integer patientId;
    private Integer consultationId;

    public static FactureSummaryDto fromEntity(Facture facture) {
        if (facture == null) {
            return null;
        }
        return FactureSummaryDto.builder()
                .id(facture.getId())
                .montant(facture.getMontant()) // Convert Float to BigDecimal
                .dateEmission(facture.getDateEmission())
                .statutPaiement(facture.getStatutPaiement())
                // Get IDs from related entities
                .patientId(facture.getPatient() != null ? facture.getPatient().getId() : null)
                .consultationId(facture.getConsultation() != null ? facture.getConsultation().getId() : null)
                .build();
    }

    // You typically don't need a `toEntity` method for a summary DTO if its purpose is solely for read operations
    // within a larger DTO (like PatientDto listing its invoices). If you do, it should also be minimal.
    public static Facture toEntity(FactureSummaryDto dto) {
        if (dto == null) return null;
        Facture entity = new Facture();
        entity.setId(dto.getId());
        entity.setMontant(dto.getMontant() != null ? dto.getMontant().floatValue() : null); // Convert BigDecimal back to Float if your entity uses Float
        entity.setDateEmission(dto.getDateEmission());
        entity.setStatutPaiement(dto.getStatutPaiement());
        // Related entities (patient, consultation) would be set in the service layer using their IDs
        return entity;
    }
}