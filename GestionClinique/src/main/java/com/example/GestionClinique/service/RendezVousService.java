package com.example.GestionClinique.service;

import com.example.GestionClinique.dto.RequestDto.RendezVousRequestDto;
import com.example.GestionClinique.model.entity.Salle;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


public interface RendezVousService {
    RendezVousRequestDto createRendezVous(RendezVousRequestDto rendezVousRequestDto);
    RendezVousRequestDto findRendezVousById(Integer id);
    RendezVousRequestDto updateRendezVous(Integer id, RendezVousRequestDto rendezVousRequestDto);
    void deleteRendezVous(Integer id);
    List<RendezVousRequestDto> findAllRendezVous();
    List<RendezVousRequestDto> findRendezVousByStatut(StatutRDV statut);

    List<RendezVousRequestDto> findRendezVousBySalleId(Integer salleId); // Renommé pour clarté
    List<RendezVousRequestDto> findRendezVousByPatientId(Integer patientId); // Renommé pour clarté
    List<RendezVousRequestDto> findRendezVousByMedecinId(Integer utilisateurId); // Renommé pour clarté

    // Nouvelle méthode: Vérifier la disponibilité d'un créneau (pour médecin et salle)
    boolean isRendezVousAvailable(LocalDate jour, LocalTime heure, Utilisateur medecin, Salle salle);

    // Nouvelle méthode: Annuler un rendez-vous avec logique de prévenance
    RendezVousRequestDto cancelRendezVous(Integer rendezVousId);

    List<RendezVousRequestDto> findRendezVousByJour(LocalDate jour);
}
