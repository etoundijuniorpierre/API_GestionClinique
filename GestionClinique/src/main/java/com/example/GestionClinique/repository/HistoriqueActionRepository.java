package com.example.GestionClinique.repository;


import com.example.GestionClinique.model.entity.HistoriqueAction;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.util.List;

@Repository
public interface HistoriqueActionRepository extends JpaRepository<HistoriqueAction, Long> {
    List<HistoriqueAction> findByUtilisateurId(Long utilisateurId); // Changed to Long for consistency
    List<HistoriqueAction> findByUtilisateurNomCompletContainingIgnoreCase(String utilisateurName); // Assuming Utilisateur has nomComplet
    List<HistoriqueAction> findByDateBetween(LocalDate startDate, LocalDate endDate); // Simplified method name
}
