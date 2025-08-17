package com.example.GestionClinique.repository;


import com.example.GestionClinique.model.entity.HistoriqueAction;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.util.List;

@Repository
public interface HistoriqueActionRepository extends JpaRepository<HistoriqueAction, Long> {

    List<HistoriqueAction> findByUtilisateurNom(String nom);


    @Query("SELECT h FROM HistoriqueAction h WHERE h.utilisateur.nom LIKE %:nomComplet% OR h.utilisateur.prenom LIKE %:nomComplet%")
    List<HistoriqueAction> findByNomCompletUtilisateur(@Param("nomComplet") String nomComplet);

    // Autres méthodes
    List<HistoriqueAction> findByUtilisateurId(Long utilisateurId);
    List<HistoriqueAction> findByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT h FROM HistoriqueAction h " +
            "WHERE (:nom IS NULL OR h.utilisateur.nom LIKE %:nom%) " +
            "AND (:prenom IS NULL OR h.utilisateur.prenom LIKE %:prenom%) " +
            "AND (:email IS NULL OR h.utilisateur.email LIKE %:email%) " +
            "AND (:motCle IS NULL OR h.action LIKE %:motCle%)")
    List<HistoriqueAction> searchHistoriqueActions(
            @Param("nom") String nom,
            @Param("prenom") String prenom,
            @Param("email") String email,
            @Param("motCle") String motCle
    );

    List<HistoriqueAction> findAllByOrderByIdDesc();
}
