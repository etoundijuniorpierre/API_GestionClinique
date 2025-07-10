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
    // Option 1: Recherche par nom simple
    List<HistoriqueAction> findByUtilisateurNom(String nom);

    // Option 2: Recherche par nom complet (si le champ existe)
    @Query("SELECT h FROM HistoriqueAction h WHERE h.utilisateur.nom LIKE %:nomComplet% OR h.utilisateur.prenom LIKE %:nomComplet%")
    List<HistoriqueAction> findByNomCompletUtilisateur(@Param("nomComplet") String nomComplet);

    // Autres méthodes
    List<HistoriqueAction> findByUtilisateurId(Long utilisateurId);
    List<HistoriqueAction> findByDateBetween(LocalDate startDate, LocalDate endDate);
}
