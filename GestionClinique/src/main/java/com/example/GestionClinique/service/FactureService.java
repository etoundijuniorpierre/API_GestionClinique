package com.example.GestionClinique.service;


import com.example.GestionClinique.dto.RequestDto.PatientRequestDto;
import com.example.GestionClinique.model.entity.Facture;
import com.example.GestionClinique.model.entity.Patient;
import com.example.GestionClinique.model.entity.enumElem.ModePaiement;
import com.example.GestionClinique.model.entity.enumElem.StatutPaiement;

import java.util.List;


public interface FactureService {
    // This method is now called from ConsultationService
    Facture generateInvoiceForConsultation(Long consultationId, ModePaiement modePaiement);

    // Existing methods
    Facture createFactureForConsultation(Long consultationId, Facture facture); // This method should be reconsidered or renamed, as generateInvoiceForConsultation now handles the creation
    Facture updateFacture(Long id, Facture factureDetails);
    List<Facture> findAllFactures();
    List<Facture> findFacturesByStatut(StatutPaiement statutPaiement);
    List<Facture> findFacturesByModePaiement(ModePaiement modePaiement);
    Facture findById(Long id);
    void deleteFacture(Long id);
    Patient findPatientByFactureId(Long id);
    Facture updateStatutPaiement(Long factureId, StatutPaiement nouveauStatut);
}
