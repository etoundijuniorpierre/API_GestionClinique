package com.example.GestionClinique.repository;

import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Collection;
import java.util.Optional;


public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {


    Collection<Utilisateur> findUtilisateurByRole_RoleType(RoleType roleType);

    Optional<Utilisateur> findUtilisateurByInfoPersonnel_Email(String email);

    Collection<Utilisateur> findUtilisateurByInfoPersonnel_Nom(String nom);



}
