package com.example.GestionClinique.model.entity;

import com.example.GestionClinique.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
// import java.time.LocalDate; // No longer needed directly here for dateCreation
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dossier_medical") // Consistent snake_case
public class DossierMedical extends BaseEntity { // Assuming BaseEntity has 'id', 'creationDate', 'lastModifiedDate'

    @Column(name = "groupe_sanguin") // Added field
    private String groupeSanguin;

    @Column(name = "antecedents_medicaux", columnDefinition = "TEXT")
    private String antecedentsMedicaux;

    @Column(name = "allergies", columnDefinition = "TEXT")
    private String allergies;

    @Column(name = "dernier_traitements", columnDefinition = "TEXT")
    private String dernierTraitement;

    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false, unique = true) // patient_id should be unique for a dossier
    private Patient patient;

    @OneToMany(mappedBy = "dossierMedical", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Consultation> consultations = new ArrayList<>();

    @OneToMany(mappedBy = "dossierMedical", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY) // Changed FetchType to LAZY
    private List<Prescription> prescriptions = new ArrayList<>();
}