package com.example.GestionClinique.model.entity;


import com.example.GestionClinique.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "consultation")
public class Consultation extends BaseEntity {

    @Column(name = "motifs", nullable = false) // Ajouté nullable = false
    private String motifs;

    @Column(name = "tensionArterielle", nullable = false) // Ajouté nullable = false
    private String tensionArterielle;

    @Column(name = "temperature", nullable = false) // Ajouté nullable = false
    private Float temperature;

    @Column(name = "poids", nullable = false) // Ajouté nullable = false
    private Float poids;

    @Column(name = "taille", nullable = false) // Ajouté nullable = false
    private Float taille;

    @Column(name = "compteRendu", nullable = false, columnDefinition = "TEXT") // Ajouté nullable = false et columnDefinition
    private String compteRendu;

    @Column(name = "diagnostic", nullable = false, columnDefinition = "TEXT") // Ajouté nullable = false et columnDefinition
    private String diagnostic;

    @ManyToOne(fetch = FetchType.LAZY) // Ajouté Lazy pour optimisation
    @JoinColumn(name = "dossier_medical_id", nullable = false) // Ajouté nullable = false
    private DossierMedical dossierMedical;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medecin_id", nullable = false) // Clé étrangère vers l'ID du médecin
    private Utilisateur medecin;


    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY) // Ajouté Lazy pour optimisation
    private List<Prescription> prescriptions = new ArrayList<>(); // Initialisation pour éviter les NullPointerExceptions

    @OneToOne(mappedBy = "consultation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY) // Ajouté Lazy pour optimisation et orphanRemoval
    private Facture facture;

    @OneToOne(fetch = FetchType.LAZY) // Ajouté Lazy pour optimisation
    @JoinColumn(name = "rendez_vous_id", unique = true, nullable = false)
    private RendezVous rendezVous;
}
