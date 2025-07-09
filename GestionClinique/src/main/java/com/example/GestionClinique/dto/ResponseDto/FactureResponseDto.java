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
@Builder
public class FactureResponseDto extends BaseResponseDto { // Assuming BaseResponseDto has ID, dates
    private Float montant;
    private String patientNomComplet; //à récupérer à partir de l'id de la consultation
    private LocalDateTime consultationDateTime; //à récupérer à partir de l'id de la consultation
    private String serviceMedicalNom; //à récupérer à partir de l'id de la consultation
    private StatutPaiement statutPaiement;
    private ModePaiement modePaiement;
}