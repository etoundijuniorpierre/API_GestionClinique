package com.example.GestionClinique.repository;


import com.example.GestionClinique.model.entity.RendezVous;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;


public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {


    List<RendezVous> findByJour(LocalDate jour);

    Optional<RendezVous> findByJourAndHeureAndSalleId(LocalDate jour, LocalTime heure, Long salleId);

    Optional<RendezVous> findByJourAndHeureAndMedecinId(LocalDate jour, LocalTime heure, Long medecinId);

    List<RendezVous> findByMedecinId(Long medecinId);

    List<RendezVous> findByPatientId(Long patientId);

    List<RendezVous> findBySalleId(Long salleId);

    List<RendezVous> findByStatut(StatutRDV statut);
}
