package com.example.GestionClinique.service;

import com.example.GestionClinique.dto.FactureDto;
import com.example.GestionClinique.dto.PatientDto;
import com.example.GestionClinique.dto.RendezVousDto;
import com.example.GestionClinique.model.entity.Facture;
import com.example.GestionClinique.model.entity.enumElem.ModePaiement;
import com.example.GestionClinique.model.entity.enumElem.StatutPaiement;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;


public interface FactureService {
//    FactureDto createFacture(FactureDto factureDto);

    // Nouvelle méthode: Créer une facture pour une consultation terminée
    FactureDto createFactureForConsultation(Integer consultationId, FactureDto factureDto);
    FactureDto updateFacture(Integer id, FactureDto factureDto);
    List<FactureDto> findAllFactures();
    List<FactureDto> findFacturesByStatut(StatutPaiement statutPaiement);
    List<FactureDto> findFacturesByModePaiement(ModePaiement modePaiement);
    FactureDto findById(Integer id);
    void deleteFacture(Integer id);
    PatientDto findPatientByFactureId(Integer id); // Renommé pour clarté

    // Nouvelle méthode: Mettre à jour spécifiquement le statut de paiement
    FactureDto updateStatutPaiement(Integer factureId, StatutPaiement nouveauStatut);
}
