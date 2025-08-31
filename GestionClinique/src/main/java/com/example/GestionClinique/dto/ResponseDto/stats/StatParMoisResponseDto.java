package com.example.GestionClinique.dto.ResponseDto.stats;

import com.example.GestionClinique.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class StatParMoisResponseDto extends BaseEntity {
    private String mois;
    private Long nbrRendezVousCONFIRME;
    private Long nbrRendezANNULE;
    private Long nbrPatientEnrg;
    private Long nbrConsultation;
    private Double revenu;
}
