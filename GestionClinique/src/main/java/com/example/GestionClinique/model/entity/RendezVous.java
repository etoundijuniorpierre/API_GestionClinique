package com.example.GestionClinique.model.entity;

import com.example.GestionClinique.model.EntityAbstracte;
import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;


@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rendezVous", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"heure", "utilisateur_id"}), // Correct: un médecin ne peut être qu'à un RV à cette date/heure
        @UniqueConstraint(columnNames = {"heure", "salle_id"}) // Correct: une salle ne peut être qu'à un RV à cette date/heure
})
public class RendezVous extends EntityAbstracte {

    @Column(name = "heure", nullable = false) // unique=true n'est pas nécessaire ici car géré par les uniqueConstraints du tableau
    private LocalTime heure;

    @Column(name = "jour", nullable = false) // unique=true n'est pas nécessaire ici car géré par les uniqueConstraints du tableau
    private LocalDate jour;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false) // Ajouté nullable = false
    private StatutRDV statut;

    @Column(name = "notes", columnDefinition = "TEXT") // Ajouté columnDefinition
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceMedical serviceMedical;

    @ManyToOne(fetch = FetchType.LAZY) // Ajouté Lazy
    @JoinColumn(name = "patient_id", nullable = false) // CORRECTION: Supprimé unique = true
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY) // Ajouté Lazy
    @JoinColumn(name = "medecin_id", nullable = false) // CORRECTION: Supprimé unique = true
    private Utilisateur medecin;

    @ManyToOne(fetch = FetchType.LAZY) // Ajouté Lazy
    @JoinColumn(name = "salle_id", nullable = false) // CORRECTION: Supprimé unique = true
    private Salle salle;

    @OneToOne(mappedBy = "rendezVous", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY) // Ajouté Lazy
    private Consultation consultation;
}
