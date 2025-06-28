package com.example.GestionClinique.service;

import com.example.GestionClinique.dto.RequestDto.UtilisateurRequestRequestDto;
import com.example.GestionClinique.model.entity.enumElem.RoleType;

import java.util.List;


public interface UtilisateurService {
//    Optional<Utilisateur> login(String email, String password);
    UtilisateurRequestRequestDto createUtilisateur(UtilisateurRequestRequestDto utilisateurRequestDto);
    UtilisateurRequestRequestDto findUtilisateurById(Integer id);
    List<UtilisateurRequestRequestDto> findAllUtilisateur();
    UtilisateurRequestRequestDto updateUtilisateur(Integer id, UtilisateurRequestRequestDto utilisateurRequestDto);
    void deleteUtilisateur(Integer id);

    // Nouvelle méthode: Trouver un utilisateur par son email (généralement unique)
    UtilisateurRequestRequestDto findUtilisateurByInfoPersonnel_Email(String email);
    // Nouvelle méthode: Trouver un utilisateur par son nom (peut retourner plusieurs)
    List<UtilisateurRequestRequestDto> findUtilisateurByInfoPersonnel_Nom(String nom);
    // Nouvelle méthode: Trouver les utilisateurs par rôle
    List<UtilisateurRequestRequestDto> findUtilisateurByRole_RoleType(RoleType roleType);
    // Nouvelle méthode: Mettre à jour le statut actif/inactif d'un utilisateur
    UtilisateurRequestRequestDto updateUtilisateurStatus(Integer id, boolean isActive);
}
