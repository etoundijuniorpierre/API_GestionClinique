package com.example.GestionClinique.model.entity;


import com.example.GestionClinique.model.EntityAbstracte;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "patient")
public class Patient extends EntityAbstracte {
    @Embedded
    private InfoPersonnel infoPersonnel;

    @OneToOne(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY) // Ajouté Lazy
    private DossierMedical dossierMedical;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RendezVous> rendezVous = new ArrayList<>(); // Initialisation

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Facture> factures = new ArrayList<>(); // Initialisation

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Prescription> prescriptions = new ArrayList<>(); // Initialisation
}
