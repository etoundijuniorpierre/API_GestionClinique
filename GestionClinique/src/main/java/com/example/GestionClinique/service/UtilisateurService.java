package com.example.GestionClinique.service;

import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.RoleType;
import com.example.GestionClinique.model.entity.RendezVous;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;

import java.util.List;


public interface UtilisateurService {

    Utilisateur createUtilisateur(Utilisateur utilisateur);
    Utilisateur findUtilisateurById(Long id);
    List<Utilisateur> findAllUtilisateur();
    Utilisateur updateUtilisateur(Long id, Utilisateur utilisateur);
    void deleteUtilisateur(Long id);
    Utilisateur findUtilisateurByEmail(String email);
    List<Utilisateur> findUtilisateurByNom(String nom);
    List<Utilisateur> findUtilisateurByRole_RoleType(RoleType roleType);
    Utilisateur updateUtilisateurStatus(Long id, boolean isActive);
    List<RendezVous> findRendezVousByMedecinSearchTerm(String medecinSearchTerm);
    List<RendezVous> findRendezVousForMedecinByStatus(String medecinName, StatutRDV statut);
    List<RendezVous> findRendezVousCONFIRMEThisDay(Long medecinId);

    List<Utilisateur> findUtisateurWithStatusCONNECTActually(Long medecinId);
    List<Utilisateur> findUtisateurWithStatusDISCONNECTActually(Long medecinId);

}
