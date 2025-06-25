// FactureDto.java (updated)
package com.example.GestionClinique.dto;

import com.example.GestionClinique.model.entity.Facture;
import com.example.GestionClinique.model.entity.enumElem.ModePaiement;
import com.example.GestionClinique.model.entity.enumElem.StatutPaiement;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor; // Add this for @Builder compatibility

import java.time.LocalDate;
import java.math.BigDecimal; // Import BigDecimal

@Data
@Builder
@AllArgsConstructor // Add this
@NoArgsConstructor // Add this
public class FactureDto {
    private Integer id;
    private Float montant; // Changed to BigDecimal
    private LocalDate dateEmission;
    private StatutPaiement statutPaiement;
    private ModePaiement modePaiement;

    // CHANGE: Use Summary DTOs or IDs for related entities to prevent recursion
    private PatientSummaryDto patientSummary;
    private ConsultationSummaryDto consultationSummary;

    public static FactureDto fromEntity(Facture facture) {
        if(facture == null) return null;

        return FactureDto.builder()
                .id(facture.getId())
                .montant(facture.getMontant()) // Convert Float to BigDecimal
                .dateEmission(facture.getDateEmission())
                .statutPaiement(facture.getStatutPaiement())
                .modePaiement(facture.getModePaiement())
                // CRITICAL CHANGE: Map to Summary DTOs to break the cycle
                .patientSummary(PatientSummaryDto.fromEntity(facture.getPatient()))
                .consultationSummary(ConsultationSummaryDto.fromEntity(facture.getConsultation()))
                .build();
    }

    public static Facture toEntity(FactureDto factureDto) {
        if(factureDto == null) return null;

        Facture facture = new Facture();
        facture.setId(factureDto.getId());
        facture.setMontant(factureDto.getMontant() != null ? factureDto.getMontant().floatValue() : null); // Convert BigDecimal back to Float
        facture.setDateEmission(factureDto.getDateEmission());
        facture.setStatutPaiement(factureDto.getStatutPaiement());
        facture.setModePaiement(factureDto.getModePaiement());


        return facture;
    }
}