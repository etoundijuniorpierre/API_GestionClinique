// FactureSummaryDto.java
package com.example.GestionClinique.dto.ResponseDto;

import com.example.GestionClinique.model.entity.enumElem.ModePaiement;
import com.example.GestionClinique.model.entity.enumElem.StatutPaiement;
import lombok.*;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FactureResponseDto extends BaseResponseDto { // Assuming BaseResponseDto has ID, dates
    private Float montant;
    private LocalDate dateEmission;
    private StatutPaiement statutPaiement;
    private ModePaiement modePaiement; // Added to response DTO

    // Include nested DTOs for related entities for a more complete response
    private PatientResponseDto patient;
    private ConsultationResponseDto consultation;
}