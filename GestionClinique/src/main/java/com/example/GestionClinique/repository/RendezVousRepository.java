package com.example.GestionClinique.repository;

import com.example.GestionClinique.model.entity.Patient;
import com.example.GestionClinique.model.entity.RendezVous;
import com.example.GestionClinique.model.entity.Salle;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface RendezVousRepository extends JpaRepository<RendezVous, Integer> {


    boolean existsBySalle(Salle salleToDelete);


    Optional<RendezVous> findBySalleAndJourAndHeure(Salle salle, LocalDate jour, LocalTime heureDebut);

    Optional<RendezVous> findByMedecinAndJourAndHeure(Utilisateur existingMedecin, LocalDate jour, LocalTime heure);

    Optional<RendezVous> findByPatientAndJourAndHeure(Patient existingPatient, LocalDate jour, LocalTime heure);

    List<RendezVous> findRendezVousByStatut(StatutRDV statut);
}
