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
public class Facture extends BaseEntity {

    @Column(name = "montant", nullable = false) // Ajouté nullable = false
    private Float montant;

    @Column(name = "dateEmission", nullable = false) // Ajouté nullable = false
    private LocalDate dateEmission;

    @Enumerated(EnumType.STRING)
    @Column(name = "statutPaiement", nullable = false) // Ajouté nullable = false
    private StatutPaiement statutPaiement;

    @Enumerated(EnumType.STRING)
    @Column(name = "modePaiement", nullable = false) // Ajouté nullable = false
    private ModePaiement modePaiement;

    @ManyToOne(fetch = FetchType.LAZY) // Ajouté Lazy
    @JoinColumn(name = "patient_id", nullable = false) // Ajouté nullable = false
    private Patient patient;

    @OneToOne(fetch = FetchType.LAZY) // Ajouté Lazy
    @JoinColumn(name = "consultation_id", nullable = false, unique = true) // Ajouté nullable = false et unique = true
    private Consultation consultation;
}
