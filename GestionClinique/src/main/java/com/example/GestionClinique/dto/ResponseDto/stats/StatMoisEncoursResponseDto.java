package com.example.GestionClinique.dto.ResponseDto.stats;

import com.example.GestionClinique.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class StatMoisEncoursResponseDto extends BaseEntity {
    private String moisEncours; // Format like "2025-07" or "Juillet 2025"
    private Long nbrRendezVousCONFIRME;
    private Long nbrRendezANNULE;
    private Long nbrPatientEnrg;
    private Long nbrConsultation;
    private Double revenu;
}
