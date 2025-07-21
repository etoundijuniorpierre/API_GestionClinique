package com.example.GestionClinique.model.entity.statistiques;

import com.example.GestionClinique.model.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class statMoisDernier extends BaseEntity {

    private String moisDernier;

    private Long nbrRendezVousCONFIRME;

    private Long nbrRendezANNULE;

    private Long nbrPatientEnrg;

    private Long nbrConsultation;

    private Long revenu;

}
