package com.example.GestionClinique.model.entity;

import com.example.GestionClinique.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "prescription")
public class Prescription extends BaseEntity {

    @Column(name = "typePrescription", nullable = false)
    private String typePrescription;

    @Column(name = "medicaments", columnDefinition = "TEXT")
    private String medicaments;

    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;

    @Column(name = "dureePrescription") // Conserve ce nom de colonne
    private String dureePrescription;

    @Column(name = "quantite")
    private Integer quantite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultation_id", nullable = false)
    private Consultation consultation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medecin_id", nullable = false)
    private Utilisateur medecin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dossierMedical_id", nullable = false)
    private DossierMedical dossierMedical;


}


