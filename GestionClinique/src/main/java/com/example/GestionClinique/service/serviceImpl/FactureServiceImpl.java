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

    /**
     * Generates and saves a new invoice for a given consultation.
     * This is the method intended to be called by ConsultationService.
     *
     * @param consultationId The ID of the consultation for which to generate the invoice.
     * @param modePaiement   The mode of payment for the invoice.
     */
    @Override
    public Facture generateInvoiceForConsultation(Long consultationId, ModePaiement modePaiement) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new IllegalArgumentException("Consultation not found with ID: " + consultationId));

        // Check if a facture already exists for this consultation to prevent duplicates
        if (factureRepository.findByConsultationId(consultationId).isPresent()) {
            throw new RuntimeException("A facture already exists for Consultation with ID: " + consultationId);
        }

        Facture facture = new Facture();
        facture.setConsultation(consultation);

        if (consultation.getDossierMedical() != null && consultation.getDossierMedical().getPatient() != null) {
            facture.setPatient(consultation.getDossierMedical().getPatient());
        } else {
            System.out.println("Warning: Creating invoice for consultation ID " + consultationId + " without an associated patient.");
            facture.setPatient(null); // Explicitly set to null if no patient
        }

        facture.setDateEmission(LocalDate.now());
        facture.setStatutPaiement(StatutPaiement.IMPAYE);
        facture.setModePaiement(modePaiement);
        facture.setMontant(consultation.getMedecin().getServiceMedical().getMontant());

        Facture savedFacture = factureRepository.save(facture);

        consultation.setFacture(savedFacture);
        consultationRepository.save(consultation);

        return savedFacture;
    }



    @Override
    public Facture createFactureForConsultation(Long consultationId, Facture facture) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new IllegalArgumentException("Consultation not found with ID: " + consultationId));

        if (factureRepository.findByConsultationId(consultationId).isPresent()) {
            throw new RuntimeException("A facture already exists for Consultation with ID: " + consultationId);
        }

        facture.setConsultation(consultation);

        // Derive patient from consultation
        // This part needs adjustment based on the consultation's patient/dossier nullable status
        if (consultation.getDossierMedical() != null && consultation.getDossierMedical().getPatient() != null) {
            facture.setPatient(consultation.getDossierMedical().getPatient());
        } else {
            // Allow patient to be null if consultation's dossierMedical/patient is null
            facture.setPatient(null);
            System.out.println("Warning: Creating invoice via createFactureForConsultation for consultation ID " + consultationId + " without an associated patient.");
        }

        if (facture.getDateEmission() == null) {
            facture.setDateEmission(LocalDate.now());
        }
        if (facture.getStatutPaiement() == null) {
            facture.setStatutPaiement(StatutPaiement.IMPAYE);
        }
        if (facture.getModePaiement() == null) {
            facture.setModePaiement(ModePaiement.ESPECES);
        }

        Facture savedFacture = factureRepository.save(facture);

        consultation.setFacture(savedFacture);
        consultationRepository.save(consultation);

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
    @Transactional
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
        // Ensure patient is not null before returning if the Facture entity allows null patients
        if (facture.getPatient() == null) {
            throw new IllegalStateException("Facture with ID: " + id + " does not have an associated patient.");
        }
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

