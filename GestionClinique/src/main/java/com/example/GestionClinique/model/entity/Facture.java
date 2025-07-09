package com.example.GestionClinique.model.entity;

import com.example.GestionClinique.model.BaseEntity;
import com.example.GestionClinique.model.entity.enumElem.ModePaiement;
import com.example.GestionClinique.model.entity.enumElem.StatutPaiement;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDate;

// Facture.java
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "facture")
public class Facture extends BaseEntity {

    @Column(name = "montant", nullable = false)
    private Double montant;

    @Column(name = "date_emission", nullable = false)
    private LocalDate dateEmission;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_paiement", nullable = false)
    private StatutPaiement statutPaiement;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode_paiement", nullable = false)
    private ModePaiement modePaiement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = true)
    private Patient patient;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultation_id", nullable = false, unique = true)
    private Consultation consultation;
}