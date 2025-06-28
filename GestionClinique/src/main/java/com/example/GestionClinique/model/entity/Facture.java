package com.example.GestionClinique.model.entity;

import com.example.GestionClinique.model.BaseEntity;
import com.example.GestionClinique.model.entity.enumElem.ModePaiement;
import com.example.GestionClinique.model.entity.enumElem.StatutPaiement;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "facture")
public class Facture extends BaseEntity { // Assuming BaseEntity provides 'id', 'creationDate', 'lastModifiedDate'

    @Column(name = "montant", nullable = false)
    private Float montant;

    @Column(name = "date_emission", nullable = false) // Consistent snake_case for column name
    private LocalDate dateEmission;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut_paiement", nullable = false) // Consistent snake_case
    private StatutPaiement statutPaiement;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode_paiement", nullable = false) // Consistent snake_case
    private ModePaiement modePaiement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultation_id", nullable = false, unique = true)
    private Consultation consultation;
}
