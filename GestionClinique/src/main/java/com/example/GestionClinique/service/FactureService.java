package com.example.GestionClinique.service;


import com.example.GestionClinique.model.entity.Facture;
import com.example.GestionClinique.model.entity.Patient;
import com.example.GestionClinique.model.entity.enumElem.ModePaiement;
import com.example.GestionClinique.model.entity.enumElem.StatutPaiement;

import java.util.List;


public interface FactureService {
    // This method is now called from ConsultationService
    void generateInvoiceForRendesVous(Long rendezVousId);

    Facture payerFacture(Long factureId, ModePaiement modePaiement);

    void generateInvoiceForConsultation(Long consultationId);

    Facture updateFacture(Long id, Facture factureDetails);
    List<Facture> findAllFactures();
    List<Facture> findFacturesByStatut(StatutPaiement statutPaiement);
    List<Facture> findFacturesByModePaiement(ModePaiement modePaiement);
    List<Facture> findAllFacturesIMPAYE();
    Facture findById(Long id);
    void deleteFacture(Long id);
    Patient findPatientByFactureId(Long id);
//    Facture updateStatutPaiement(Long factureId, StatutPaiement nouveauStatut);
}
