package com.example.GestionClinique.model.entity;

import com.example.GestionClinique.model.BaseEntity;
import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;



@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rendez_vous", uniqueConstraints = { // Changed table name to snake_case for convention
        @UniqueConstraint(columnNames = {"jour", "heure", "medecin_id"}), // Corrected: user_id -> medecin_id
        @UniqueConstraint(columnNames = {"jour", "heure", "salle_id"}) // Correct: jour + heure + salle = unique
})
public class RendezVous extends BaseEntity {

    @Column(name = "heure", nullable = false)
    private LocalTime heure;

    @Column(name = "jour", nullable = false)
    private LocalDate jour;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutRDV statut = StatutRDV.EN_ATTENTE;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_medical", nullable = false)
    private ServiceMedical serviceMedical;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medecin_id", nullable = false)
    private Utilisateur medecin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salle_id", nullable = false)
    private Salle salle;

    @OneToOne(mappedBy = "rendezVous", cascade = CascadeType.ALL, orphanRemoval = true)
    private Facture facture;

    @OneToOne(mappedBy = "rendezVous", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Consultation consultation;
}
