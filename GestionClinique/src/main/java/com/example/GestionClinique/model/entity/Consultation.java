package com.example.GestionClinique.model.entity;


import com.example.GestionClinique.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "consultation")
public class Consultation extends BaseEntity {

    @Column(name = "poids", nullable = false)
    private Float poids;

    @Column(name = "taille", nullable = false)
    private Float taille;

    @Column(name = "temperature", nullable = false)
    private Float temperature;

    @Column(name = "tension_arterielle", nullable = false)
    private String tensionArterielle;


    @Column(name = "motifs", nullable = false)
    private String motifs;

    @Column(name = "compte_rendu", nullable = false, columnDefinition = "TEXT")
    private String compteRendu;

    @Column(name = "diagnostic", nullable = false, columnDefinition = "TEXT")
    private String diagnostic;

//    @Column(name = "date_heure_debut", nullable = false)
//    private LocalDateTime dateHeureDebut;
//
//    @Column(name = "duree_minutes", nullable = false)
//    private Long dureeMinutes;
//
//    @Transient
//    public LocalDateTime getDateHeureFin() {
//        if (creationDate != null && dureeMinutes != null) {
//            return creationDate.plusMinutes(dureeMinutes);
//        }
//        return null;
//    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dossier_medical_id", nullable = true)
    private DossierMedical dossierMedical;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medecin_id", nullable = false)
    private Utilisateur medecin;

    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Prescription> prescriptions = new ArrayList<>();

    @OneToOne(mappedBy = "consultation", cascade = CascadeType.ALL, orphanRemoval = true)
    private Facture facture;

    // This is the crucial change: rendez_vous_id can now be null
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rendez_vous_id", unique = true, nullable = true) // Set to nullable = true
    private RendezVous rendezVous;
}
