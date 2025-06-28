package com.example.GestionClinique.service.serviceImpl;


import com.example.GestionClinique.model.entity.Consultation;
import com.example.GestionClinique.model.entity.Facture;
import com.example.GestionClinique.model.entity.Patient; // Need to import Patient entity
import com.example.GestionClinique.model.entity.enumElem.ModePaiement;
import com.example.GestionClinique.model.entity.enumElem.StatutPaiement;
import com.example.GestionClinique.repository.ConsultationRepository;
import com.example.GestionClinique.repository.FactureRepository;
import com.example.GestionClinique.service.FactureService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
@Transactional
public class FactureServiceImpl implements FactureService {

    private final FactureRepository factureRepository;
    private final ConsultationRepository consultationRepository; // To fetch Consultation

    @Autowired
    public FactureServiceImpl(FactureRepository factureRepository, ConsultationRepository consultationRepository) {
        this.factureRepository = factureRepository;
        this.consultationRepository = consultationRepository;
    }

    @Override
    public Facture createFactureForConsultation(Long consultationId, Facture facture) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new IllegalArgumentException("Consultation not found with ID: " + consultationId));

        // Check if a facture already exists for this consultation
        if (factureRepository.findByConsultationId(consultationId).isPresent()) {
            throw new RuntimeException("A facture already exists for Consultation with ID: " + consultationId);
        }

        // Link the consultation to the facture
        facture.setConsultation(consultation);

        // Derive patient from consultation
        if (consultation.getDossierMedical().getPatient()== null) { // Assuming Consultation entity has a getPatient() method
            throw new RuntimeException("Consultation does not have an associated patient.");
        }
        facture.setPatient(consultation.getDossierMedical().getPatient());

        // Set dateEmission if not provided in the DTO
        if (facture.getDateEmission() == null) {
            facture.setDateEmission(LocalDate.now());
        }
        // Set default status if not provided (e.g., IMPAYE)
        if (facture.getStatutPaiement() == null) {
            facture.setStatutPaiement(StatutPaiement.IMPAYE);
        }
        // Set default mode if not provided (e.g., ESPECES)
        if (facture.getModePaiement() == null) {
            facture.setModePaiement(ModePaiement.ESPECES);
        }


        Facture savedFacture = factureRepository.save(facture);

        // Update the Consultation to link to this new Facture (bi-directional relationship)
        consultation.setFacture(savedFacture);
        consultationRepository.save(consultation); // Save updated consultation

        return savedFacture;
    }

    @Override
    public Facture updateFacture(Long id, Facture factureDetails) {
        Facture existingFacture = factureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Facture not found with ID: " + id));

        existingFacture.setMontant(factureDetails.getMontant());
        existingFacture.setDateEmission(factureDetails.getDateEmission());
        existingFacture.setStatutPaiement(factureDetails.getStatutPaiement());
        existingFacture.setModePaiement(factureDetails.getModePaiement());

        // Patient and Consultation associations are generally not updated here.
        // If they need to be updated, dedicated methods or more complex logic would be required.

        return factureRepository.save(existingFacture);
    }

    @Override
    @Transactional
    public List<Facture> findAllFactures() {
        return factureRepository.findAll();
    }

    @Override
    @Transactional
    public List<Facture> findFacturesByStatut(StatutPaiement statutPaiement) {
        return factureRepository.findByStatutPaiement(statutPaiement);
    }

    @Override
    @Transactional
    public List<Facture> findFacturesByModePaiement(ModePaiement modePaiement) {
        return factureRepository.findByModePaiement(modePaiement);
    }

    @Override
    @Transactional
    public Facture findById(Long id) {
        return factureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Facture not found with ID: " + id));
    }

    @Override
    public void deleteFacture(Long id) {
        Facture facture = factureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Facture not found with ID: " + id));

        // Disassociate facture from consultation before deleting (bi-directional link)
        if (facture.getConsultation() != null) {
            Consultation consultation = facture.getConsultation();
            consultation.setFacture(null);
            consultationRepository.save(consultation);
        }

        factureRepository.delete(facture);
    }

    @Override
    @Transactional
    public Patient findPatientByFactureId(Long id) {
        Facture facture = findById(id);
        return facture.getPatient();
    }

    @Override
    public Facture updateStatutPaiement(Long factureId, StatutPaiement nouveauStatut) {
        Facture facture = factureRepository.findById(factureId)
                .orElseThrow(() -> new IllegalArgumentException("Facture not found with ID: " + factureId));
        facture.setStatutPaiement(nouveauStatut);
        return factureRepository.save(facture);
    }
}