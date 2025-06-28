// FactureDto.java (updated)
package com.example.GestionClinique.dto.RequestDto;

import com.example.GestionClinique.model.entity.enumElem.ModePaiement;
import com.example.GestionClinique.model.entity.enumElem.StatutPaiement;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FactureRequestDto {
    private Float montant;
    private LocalDate dateEmission;
    private StatutPaiement statutPaiement;
    private ModePaiement modePaiement;
    private Long patientId;
    private Long consultationId;
}