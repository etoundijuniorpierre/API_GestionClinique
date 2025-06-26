package com.example.GestionClinique.service;

import com.example.GestionClinique.dto.UtilisateurDto;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.RoleType;

import java.util.List;
import java.util.Optional;


public interface UtilisateurService {
//    Optional<Utilisateur> login(String email, String password);
    UtilisateurDto createUtilisateur(UtilisateurDto utilisateurDto);
    UtilisateurDto findUtilisateurById(Integer id);
    List<UtilisateurDto> findAllUtilisateur();
    UtilisateurDto updateUtilisateur(Integer id, UtilisateurDto utilisateurDto);
    void deleteUtilisateur(Integer id);

    // Nouvelle méthode: Trouver un utilisateur par son email (généralement unique)
    UtilisateurDto findUtilisateurByInfoPersonnel_Email(String email);
    // Nouvelle méthode: Trouver un utilisateur par son nom (peut retourner plusieurs)
    List<UtilisateurDto> findUtilisateurByInfoPersonnel_Nom(String nom);
    // Nouvelle méthode: Trouver les utilisateurs par rôle
    List<UtilisateurDto> findUtilisateurByRole_RoleType(RoleType roleType);
    // Nouvelle méthode: Mettre à jour le statut actif/inactif d'un utilisateur
    UtilisateurDto updateUtilisateurStatus(Integer id, boolean isActive);
}
