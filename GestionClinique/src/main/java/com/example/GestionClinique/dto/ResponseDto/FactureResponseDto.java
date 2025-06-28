// FactureSummaryDto.java
package com.example.GestionClinique.dto.ResponseDto;

import com.example.GestionClinique.model.entity.enumElem.StatutPaiement;
import lombok.*;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class FactureResponseDto extends BaseResponseDto {
    private Float montant;
    private LocalDate dateEmission;
    private StatutPaiement statutPaiement;
    private PatientResponseDto patient;
    private ConsultationResponseDto consultation;
}