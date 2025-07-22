package com.example.GestionClinique.model.entity.stats;

import com.example.GestionClinique.model.BaseEntity;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Year;


@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class StatsSurLannee extends BaseEntity {

    private String annee = Year.now().toString();

    private Long nbrRendezVousCONFIRME;

    private Long nbrRendezANNULE;

    private Long nbrPatientEnrg;

    private Long nbrConsultation;

    private Double revenu;
}
