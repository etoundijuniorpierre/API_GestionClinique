package com.example.GestionClinique.service;

import com.example.GestionClinique.dto.RequestDto.FactureRequestDto;
import com.example.GestionClinique.dto.RequestDto.PatientRequestDto;
import com.example.GestionClinique.model.entity.enumElem.ModePaiement;
import com.example.GestionClinique.model.entity.enumElem.StatutPaiement;

import java.util.List;


public interface FactureService {
//    FactureDto createFacture(FactureDto factureDto);

    // Nouvelle méthode: Créer une facture pour une consultation terminée
    FactureRequestDto createFactureForConsultation(Integer consultationId, FactureRequestDto factureRequestDto);
    FactureRequestDto updateFacture(Integer id, FactureRequestDto factureRequestDto);
    List<FactureRequestDto> findAllFactures();
    List<FactureRequestDto> findFacturesByStatut(StatutPaiement statutPaiement);
    List<FactureRequestDto> findFacturesByModePaiement(ModePaiement modePaiement);
    FactureRequestDto findById(Integer id);
    void deleteFacture(Integer id);
    PatientRequestDto findPatientByFactureId(Integer id); // Renommé pour clarté

    // Nouvelle méthode: Mettre à jour spécifiquement le statut de paiement
    FactureRequestDto updateStatutPaiement(Integer factureId, StatutPaiement nouveauStatut);
}
