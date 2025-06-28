package com.example.GestionClinique.service;

import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.RoleType;

import java.util.List;


public interface UtilisateurService {
//    Optional<Utilisateur> login(String email, String password);
    Utilisateur createUtilisateur(Utilisateur utilisateur);
    Utilisateur findUtilisateurById(Integer id);
    List<Utilisateur> findAllUtilisateur();
    Utilisateur updateUtilisateur(Integer id, Utilisateur utilisateur);
    void deleteUtilisateur(Integer id);

    // Nouvelle méthode: Trouver un utilisateur par son email (généralement unique)
    Utilisateur findUtilisateurByEmail(String email);
    // Nouvelle méthode: Trouver un utilisateur par son nom (peut retourner plusieurs)
    List<Utilisateur> findUtilisateurByNom(String nom);
    // Nouvelle méthode: Trouver les utilisateurs par rôle
    List<Utilisateur> findUtilisateurByRole_RoleType(RoleType roleType);
    // Nouvelle méthode: Mettre à jour le statut actif/inactif d'un utilisateur
    Utilisateur updateUtilisateurStatus(Integer id, boolean isActive);
}
