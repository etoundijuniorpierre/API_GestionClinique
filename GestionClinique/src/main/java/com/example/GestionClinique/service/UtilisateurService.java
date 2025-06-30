package com.example.GestionClinique.service;

import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.RoleType;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

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

//    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
