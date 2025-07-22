// FactureDto.java (updated)
package com.example.GestionClinique.dto.RequestDto;

import com.example.GestionClinique.model.entity.enumElem.ModePaiement;
import com.example.GestionClinique.model.entity.enumElem.StatutPaiement;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FactureRequestDto {

    @NotNull
    private Long rendezVousId;

    @NotNull(message = "Le montant est requis.")
    @Positive(message = "Le montant doit être positif.")
    private Double montant;

    private LocalDate dateEmission;

    @NotNull(message = "Le statut de paiement est requis.")
    private StatutPaiement statutPaiement = StatutPaiement.IMPAYEE;

    @NotNull(message = "Le mode de paiement est requis.")
    private ModePaiement modePaiement = ModePaiement.ESPECES;
}