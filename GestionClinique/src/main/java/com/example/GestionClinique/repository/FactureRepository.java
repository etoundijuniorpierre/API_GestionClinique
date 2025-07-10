package com.example.GestionClinique.repository;

import com.example.GestionClinique.model.entity.Facture;
import com.example.GestionClinique.model.entity.enumElem.ModePaiement;
import com.example.GestionClinique.model.entity.enumElem.StatutPaiement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface FactureRepository extends JpaRepository<Facture, Long> {
    List<Facture> findByStatutPaiement(StatutPaiement statutPaiement);
    List<Facture> findByModePaiement(ModePaiement modePaiement);
    Optional<Facture> findByConsultationId(Long consultationId);
}
