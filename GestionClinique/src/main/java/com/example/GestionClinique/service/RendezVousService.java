package com.example.GestionClinique.service;

import com.example.GestionClinique.dto.RendezVousDto;
import com.example.GestionClinique.model.entity.Salle;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


public interface RendezVousService {
    RendezVousDto createRendezVous(RendezVousDto rendezVousDto);
    RendezVousDto findRendezVousById(Integer id);
    RendezVousDto updateRendezVous(Integer id, RendezVousDto rendezVousDto);
    void deleteRendezVous(Integer id);
    List<RendezVousDto> findAllRendezVous();
    List<RendezVousDto> findRendezVousByStatut(StatutRDV statut);

    List<RendezVousDto> findRendezVousBySalleId(Integer salleId); // Renommé pour clarté
    List<RendezVousDto> findRendezVousByPatientId(Integer patientId); // Renommé pour clarté
    List<RendezVousDto> findRendezVousByMedecinId(Integer utilisateurId); // Renommé pour clarté

    // Nouvelle méthode: Vérifier la disponibilité d'un créneau (pour médecin et salle)
    boolean isRendezVousAvailable(LocalDate jour, LocalTime heure, Utilisateur medecin, Salle salle);

    // Nouvelle méthode: Annuler un rendez-vous avec logique de prévenance
    RendezVousDto cancelRendezVous(Integer rendezVousId);

    List<RendezVousDto> findRendezVousByJour(LocalDate jour);
}
