package com.example.GestionClinique.model.entity;

import com.example.GestionClinique.model.EntityAbstracte;
import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dossierMedical") // Nom de table cohérent avec la casse
public class DossierMedical extends EntityAbstracte {

    @Column(name = "antecedents", columnDefinition = "TEXT") // Ajouté columnDefinition pour texte long
    private String antecedents;

    @Column(name = "allergies", columnDefinition = "TEXT") // Ajouté columnDefinition
    private String allergies;

    @Column(name = "traitementsEnCours", columnDefinition = "TEXT") // Ajouté columnDefinition
    private String traitementsEnCours;

    @Column(name = "observations", columnDefinition = "TEXT") // Ajouté columnDefinition
    private String observations;

    @OneToOne(fetch = FetchType.LAZY) // Ajouté Lazy pour optimisation
    @JoinColumn(name = "patient_id", nullable = false) // Ajouté nullable = false
    private Patient patient;

    @OneToMany(mappedBy = "dossierMedical", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Consultation> consultations = new ArrayList<>();

    @OneToMany(mappedBy = "dossierMedical", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Prescription> prescriptions = new ArrayList<>();
}