package com.example.GestionClinique.repository;

import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.RoleType;
import com.example.GestionClinique.model.entity.enumElem.StatusConnect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;
import java.util.Optional;


public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

     Optional<Utilisateur> findByEmail(String email);

    List<Utilisateur> findByRole_RoleType(RoleType roleType);

    List<Utilisateur> findByNom(String nom);

    List<Utilisateur> findByStatusConnect(StatusConnect status);

    @Query("SELECT u FROM Utilisateur u WHERE " +
            "LOWER(u.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.prenom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.telephone) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "UPPER(u.role) = UPPER(:searchTerm) OR " +
            "LOWER(u.serviceMedical) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Utilisateur> searchByTerm(@Param("searchTerm") String searchTerm);

    List<Utilisateur> findByStatusConnectOrderByLastLoginDateDesc(StatusConnect status);
    List<Utilisateur> findByStatusConnectOrderByLastLogoutDateDesc(StatusConnect status);
}
