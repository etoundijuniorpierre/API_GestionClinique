package com.example.GestionClinique.model.entity.stats;

import com.example.GestionClinique.model.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class StatMoisDernier extends BaseEntity {

    private String moisDernier;

    private Long nbrRendezVousCONFIRME;

    private Long nbrRendezANNULE;

    private Long nbrPatientEnrg;

    private Long nbrConsultation;

    private Double revenu;

}
