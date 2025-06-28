package com.example.GestionClinique.service;

import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.RoleType;

import java.util.List;


public interface UtilisateurService {
//    Optional<Utilisateur> login(String email, String password);
    Utilisateur createUtilisateur(Utilisateur utilisateur);
    Utilisateur findUtilisateurById(Long id);
    List<Utilisateur> findAllUtilisateur();
    Utilisateur updateUtilisateur(Long id, Utilisateur utilisateur);
    void deleteUtilisateur(Long id);


    Utilisateur findUtilisateurByEmail(String email);
    
    List<Utilisateur> findUtilisateurByNom(String nom);

    List<Utilisateur> findUtilisateurByRole_RoleType(RoleType roleType);

    Utilisateur updateUtilisateurStatus(Long id, boolean isActive);
}
