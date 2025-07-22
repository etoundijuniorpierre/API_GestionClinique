// FactureSummaryDto.java
package com.example.GestionClinique.dto.ResponseDto;

import com.example.GestionClinique.model.entity.enumElem.ModePaiement;
import com.example.GestionClinique.model.entity.enumElem.StatutPaiement;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FactureResponseDto extends BaseResponseDto {
    private Double montant;
    private String patientNomComplet;
    private LocalDateTime dateEmission;
    private String serviceMedicalNom;
    private StatutPaiement statutPaiement;
    private ModePaiement modePaiement;
}