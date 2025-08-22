package com.example.GestionClinique.model.entity;

import com.example.GestionClinique.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor // Add @NoArgsConstructor for JPA and Builder
@AllArgsConstructor // Add @AllArgsConstructor for Builder
@Table(name = "prescription") // Snake_case table name is good.
public class Prescription extends BaseEntity {

//    @Column(name = "date_prescription", nullable = false) // Add date_prescription as a column
//    private LocalDate datePrescription;

    @Column(name = "type_prescription", nullable = false) // Changed to snake_case
    private String typePrescription;

    @Column(name = "medicaments", columnDefinition = "TEXT")
    private String medicaments;

    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;

    @Column(name = "duree_prescription") // Changed to snake_case
    private String dureePrescription;

    @Column(name = "quantite", nullable = false) // Quantité should probably be non-nullable
    private Integer quantite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultation_id", nullable = false)
    private Consultation consultation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medecin_id", nullable = false)
    private Utilisateur medecin; // This is a Utilisateur acting as a doctor

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dossier_medical_id") // Changed to snake_case
    private DossierMedical dossierMedical;
}


