package com.example.GestionClinique.service;

import com.example.GestionClinique.dto.RequestDto.HistoriqueActionRequestDto;
import com.example.GestionClinique.model.entity.Utilisateur;

import java.time.LocalDate;
import java.util.List;


public interface HistoriqueActionService {
    void enregistrerAction(String actionDescription);
    void enregistrerAction(String actionDescription, Utilisateur utilisateur);
    List<HistoriqueActionRequestDto> findAll();
    HistoriqueActionRequestDto findById(Integer id);
    List<HistoriqueActionRequestDto> findHistoriqueByUtilisateurId(Integer id);

    List<HistoriqueActionRequestDto> findHistoriqueByUtilisateurName(String utilisateurName);
    // Nouvelle méthode: Filtrer par plage de dates
    List<HistoriqueActionRequestDto> findByDateAfterAndDateBefore(LocalDate startDate, LocalDate endDate);
    // Nouvelle méthode: Filtrer par type d'action (nécessite un champ typeAction dans l'HistoriqueActionDto)
    // List<HistoriqueActionDto> findHistoriqueByActionType(String actionType);

}
