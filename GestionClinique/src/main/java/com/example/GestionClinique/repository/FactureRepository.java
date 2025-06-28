package com.example.GestionClinique.repository;

import com.example.GestionClinique.model.entity.Facture;
import com.example.GestionClinique.model.entity.enumElem.ModePaiement;
import com.example.GestionClinique.model.entity.enumElem.StatutPaiement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;


public interface FactureRepository extends JpaRepository<Facture, Integer> {


    Collection<Facture> findFacturesByStatutPaiement(StatutPaiement statutPaiement);

    Collection<Facture> findFacturesByModePaiement(ModePaiement modePaiement);
}
