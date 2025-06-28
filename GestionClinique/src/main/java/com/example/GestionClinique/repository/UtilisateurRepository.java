package com.example.GestionClinique.repository;

import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;


public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {


//    Collection<Utilisateur> findUtilisateurByRole_RoleType(RoleType roleType);
//
//    Collection<Utilisateur> findUtilisateurByInfoPersonnel_Nom(String nom);


    Optional<Utilisateur> findByEmail(String email);

    List<Utilisateur> findByRole_RoleType(RoleType roleType);

    List<Utilisateur> findByNom(String nom);
}
