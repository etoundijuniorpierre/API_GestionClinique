package com.example.GestionClinique.service.serviceImpl;

import com.example.GestionClinique.dto.ConsultationSummaryDto; // Make sure to import this
import com.example.GestionClinique.dto.FactureDto;
import com.example.GestionClinique.dto.PatientDto; // Still needed for findPatientByFactureId
import com.example.GestionClinique.model.entity.Consultation;
import com.example.GestionClinique.model.entity.Facture;
import com.example.GestionClinique.model.entity.Patient; // Need to import Patient entity
import com.example.GestionClinique.model.entity.enumElem.ModePaiement;
import com.example.GestionClinique.model.entity.enumElem.StatutPaiement;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import com.example.GestionClinique.repository.ConsultationRepository;
import com.example.GestionClinique.repository.FactureRepository;
import com.example.GestionClinique.repository.PatientRepository; // Need PatientRepository for fetching Patient entity
import com.example.GestionClinique.service.FactureService;
import com.example.GestionClinique.service.HistoriqueActionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal; // Import BigDecimal
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FactureServiceImpl implements FactureService {
    private final FactureRepository factureRepository;
    private final ConsultationRepository consultationRepository;
    private final PatientRepository patientRepository; // Inject PatientRepository
    private final HistoriqueActionService historiqueActionService;

    @Autowired
    public FactureServiceImpl(FactureRepository factureRepository,
                              PatientRepository patientRepository, // Keep this in constructor
                              ConsultationRepository consultationRepository,
                              HistoriqueActionService historiqueActionService) {
        this.factureRepository = factureRepository;
        this.patientRepository = patientRepository; // Initialize it
        this.consultationRepository = consultationRepository;
        this.historiqueActionService = historiqueActionService;
    }

    @Override
    @Transactional
    public FactureDto updateFacture(Integer factureId, FactureDto factureDto) {
        Facture existingFacture = factureRepository.findById(factureId)
                .orElseThrow(() -> new EntityNotFoundException("La facture avec l'ID " + factureId + " n'existe pas."));

        // Update direct attributes. Use floatValue() to convert BigDecimal back if entity uses Float.
        existingFacture.setMontant(factureDto.getMontant() != null ? factureDto.getMontant().floatValue() : null);
        existingFacture.setDateEmission(factureDto.getDateEmission());
        existingFacture.setStatutPaiement(factureDto.getStatutPaiement());
        existingFacture.setModePaiement(factureDto.getModePaiement());

        // Handle related entities from summary DTOs
        // Update Patient if provided
        if (factureDto.getPatientSummary() != null && factureDto.getPatientSummary().getId() != null) {
            Patient patient = patientRepository.findById(factureDto.getPatientSummary().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Patient associé introuvable avec l'ID: " + factureDto.getPatientSummary().getId()));
            existingFacture.setPatient(patient);
        } else if (factureDto.getPatientSummary() != null) {
            existingFacture.setPatient(null); // Explicitly disassociate patient
        }

        // Update Consultation if provided
        if (factureDto.getConsultationSummary() != null && factureDto.getConsultationSummary().getId() != null) {
            Consultation consultation = consultationRepository.findById(factureDto.getConsultationSummary().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Consultation associée introuvable avec l'ID: " + factureDto.getConsultationSummary().getId()));
            existingFacture.setConsultation(consultation);
        } else if (factureDto.getConsultationSummary() != null) {
            existingFacture.setConsultation(null); // Explicitly disassociate consultation
        }


        FactureDto updatedFactureDto = FactureDto.fromEntity(
                factureRepository.save(existingFacture)
        );

        historiqueActionService.enregistrerAction(
                "Facture ID " + factureId + " mise à jour."
        );

        return updatedFactureDto;
    }

    @Override
    public List<FactureDto> findAllFactures() {
        List<FactureDto> factures = factureRepository.findAll()
                .stream()
                .map(FactureDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction("Liste de toutes les factures récupérée.");

        return factures;
    }

    @Override
    public List<FactureDto> findFacturesByStatut(StatutPaiement statutPaiement) {
        List<FactureDto> factures = factureRepository.findFacturesByStatutPaiement(statutPaiement)
                .stream()
                .map(FactureDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction("Factures récupérées avec le statut : " + statutPaiement);

        return factures;
    }

    @Override
    public List<FactureDto> findFacturesByModePaiement(ModePaiement modePaiement) {
        List<FactureDto> factures = factureRepository.findFacturesByModePaiement(modePaiement)
                .stream()
                .map(FactureDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction("Factures récupérées avec le mode de paiement : " + modePaiement);

        return factures;
    }

    @Override
    public FactureDto findById(Integer FactureId) {
        FactureDto factureDto = factureRepository.findById(FactureId)
                .map(FactureDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Facture avec l'ID : " + FactureId + " non trouvée."));

        historiqueActionService.enregistrerAction("Facture ID " + FactureId + " récupérée.");

        return factureDto;
    }

    @Override
    public void deleteFacture(Integer FactureId) {
        Facture factureToDelete = factureRepository.findById(FactureId)
                .orElseThrow(() -> new EntityNotFoundException("La facture avec l'ID : " + FactureId + " n'existe pas et ne peut pas être supprimée."));



        factureRepository.deleteById(FactureId);

        historiqueActionService.enregistrerAction("Facture ID " + FactureId + " supprimée.");
    }

    @Override
    public PatientDto findPatientByFactureId(Integer factureId) {
        Facture facture = factureRepository.findById(factureId)
                .orElseThrow(() -> new EntityNotFoundException("Facture avec l'ID : " + factureId + " non trouvée."));

        PatientDto patientDto = Optional.ofNullable(facture.getPatient())
                .map(PatientDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Aucun patient associé à la facture avec l'ID : " + factureId));

        historiqueActionService.enregistrerAction("Patient récupéré pour la facture ID : " + factureId);

        return patientDto;
    }

    @Override
    @Transactional
    public FactureDto createFactureForConsultation(Integer consultationId, FactureDto factureDto) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new EntityNotFoundException("Consultation avec l'ID : " + consultationId + " non trouvée."));

        // Ensure the rendezvous exists and is TERMINÉ for facturation
        // Assuming StatutRDV.TERMINE means the consultation is complete.
        // Your current code checks for StatutRDV.CONFIRME, which might not be correct for a finished consultation.
        // Let's assume you meant StatutRDV.TERMINE.
        if (consultation.getRendezVous() == null || consultation.getRendezVous().getStatut() != StatutRDV.TERMINE) {
            throw new IllegalStateException("Impossible de créer la facture : le rendez-vous de la consultation n'est pas terminé (Statut: " + (consultation.getRendezVous() != null ? consultation.getRendezVous().getStatut() : "N/A") + ").");
        }

        if (consultation.getFacture() != null) {
            throw new IllegalStateException("Impossible de créer la facture : la consultation avec l'ID " + consultationId + " a déjà une facture associée.");
        }

        Facture newFacture = new Facture();
        // Set direct attributes from the DTO, converting BigDecimal to Float if necessary
        newFacture.setMontant(factureDto.getMontant() != null ? factureDto.getMontant().floatValue() : null);
        newFacture.setDateEmission(factureDto.getDateEmission() != null ? factureDto.getDateEmission() : LocalDate.now());
        newFacture.setStatutPaiement(factureDto.getStatutPaiement() != null ? factureDto.getStatutPaiement() : StatutPaiement.NONPAYE);
        newFacture.setModePaiement(factureDto.getModePaiement() != null ? factureDto.getModePaiement() : ModePaiement.ESPECE);


        // Establish relationships by fetching entities
        newFacture.setConsultation(consultation);
        newFacture.setPatient(consultation.getDossierMedical().getPatient()); // Patient from consultation's dossier

        // Optional: If you want to automatically set the consultation's `facture` field:
        consultation.setFacture(newFacture); // Link the facture back to the consultation
        consultationRepository.save(consultation); // Save consultation to persist the bidirectional link

        Facture createdFacture = factureRepository.save(newFacture);

        historiqueActionService.enregistrerAction(
                "Facture créée pour la consultation ID : " + consultationId + ", Facture ID: " + createdFacture.getId()
        );

        return FactureDto.fromEntity(createdFacture);
    }

    @Override
    public FactureDto updateStatutPaiement(Integer factureId, StatutPaiement nouveauStatut) {
        Facture existingFacture = factureRepository.findById(factureId)
                .orElseThrow(() -> new EntityNotFoundException("La facture avec l'ID " + factureId + " n'existe pas."));

        existingFacture.setStatutPaiement(nouveauStatut);

        FactureDto updatedFactureDto = FactureDto.fromEntity(
                factureRepository.save(existingFacture)
        );

        historiqueActionService.enregistrerAction(
                "Statut de paiement de la facture ID " + factureId + " mis à jour à : " + nouveauStatut
        );

        return updatedFactureDto;
    }
}