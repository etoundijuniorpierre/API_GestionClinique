package com.example.GestionClinique.service;

import com.example.GestionClinique.dto.HistoriqueActionDto;
import com.example.GestionClinique.model.entity.Utilisateur;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public interface HistoriqueActionService {
    void enregistrerAction(String actionDescription);
    void enregistrerAction(String actionDescription, Utilisateur utilisateur);
    List<HistoriqueActionDto> findAll();
    HistoriqueActionDto findById(Integer id);
    List<HistoriqueActionDto> findHistoriqueByUtilisateurId(Integer id);

    List<HistoriqueActionDto> findHistoriqueByUtilisateurName(String utilisateurName);
    // Nouvelle méthode: Filtrer par plage de dates
    List<HistoriqueActionDto> findByDateAfterAndDateBefore(LocalDate startDate, LocalDate endDate);
    // Nouvelle méthode: Filtrer par type d'action (nécessite un champ typeAction dans l'HistoriqueActionDto)
    // List<HistoriqueActionDto> findHistoriqueByActionType(String actionType);

}
