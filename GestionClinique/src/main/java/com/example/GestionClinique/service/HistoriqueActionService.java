package com.example.GestionClinique.service;


import com.example.GestionClinique.model.entity.HistoriqueAction;
import com.example.GestionClinique.model.entity.Utilisateur;

import java.time.LocalDate;
import java.util.List;


public interface HistoriqueActionService {
    // Methods for recording actions (internal use, no direct DTO conversion needed here)
    HistoriqueAction enregistrerAction(String actionDescription, Long utilisateurId); // Specific for logging with ID
    HistoriqueAction enregistrerAction(String actionDescription, Utilisateur utilisateur); // For when Utilisateur entity is available
    HistoriqueAction enregistrerAction(String actionDescription); // For actions not tied to a specific user or user context unknown

    // Methods for retrieving actions (return entities)
    List<HistoriqueAction> findAllHistoriqueActionsDesc();
    HistoriqueAction findHistoriqueActionById(Long id);
    List<HistoriqueAction> findHistoriqueActionsByUtilisateurId(Long utilisateurId); // Changed to Long
    List<HistoriqueAction> findHistoriqueActionsByUtilisateurName(String utilisateurName);
    List<HistoriqueAction> findHistoriqueActionsByDateRange(LocalDate startDate, LocalDate endDate);
    List<HistoriqueAction> rechercherHistorique(String nom, String prenom, String email, String motCle);
}
