package com.example.GestionClinique.service.serviceImpl;

import com.example.GestionClinique.dto.RequestDto.FactureRequestDto;
import com.example.GestionClinique.dto.RequestDto.PatientRequestDto; // Still needed for findPatientByFactureId
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
    public FactureRequestDto updateFacture(Integer factureId, FactureRequestDto factureRequestDto) {
        Facture existingFacture = factureRepository.findById(factureId)
                .orElseThrow(() -> new EntityNotFoundException("La facture avec l'ID " + factureId + " n'existe pas."));

        // Update direct attributes. Use floatValue() to convert BigDecimal back if entity uses Float.
        existingFacture.setMontant(factureRequestDto.getMontant() != null ? factureRequestDto.getMontant().floatValue() : null);
        existingFacture.setDateEmission(factureRequestDto.getDateEmission());
        existingFacture.setStatutPaiement(factureRequestDto.getStatutPaiement());
        existingFacture.setModePaiement(factureRequestDto.getModePaiement());

        // Handle related entities from summary DTOs
        // Update Patient if provided
        if (factureRequestDto.getPatientSummary() != null && factureRequestDto.getPatientSummary().getId() != null) {
            Patient patient = patientRepository.findById(factureRequestDto.getPatientSummary().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Patient associé introuvable avec l'ID: " + factureRequestDto.getPatientSummary().getId()));
            existingFacture.setPatient(patient);
        } else if (factureRequestDto.getPatientSummary() != null) {
            existingFacture.setPatient(null); // Explicitly disassociate patient
        }

        // Update Consultation if provided
        if (factureRequestDto.getConsultationSummary() != null && factureRequestDto.getConsultationSummary().getId() != null) {
            Consultation consultation = consultationRepository.findById(factureRequestDto.getConsultationSummary().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Consultation associée introuvable avec l'ID: " + factureRequestDto.getConsultationSummary().getId()));
            existingFacture.setConsultation(consultation);
        } else if (factureRequestDto.getConsultationSummary() != null) {
            existingFacture.setConsultation(null); // Explicitly disassociate consultation
        }


        FactureRequestDto updatedFactureRequestDto = FactureRequestDto.fromEntity(
                factureRepository.save(existingFacture)
        );

        historiqueActionService.enregistrerAction(
                "Facture ID " + factureId + " mise à jour."
        );

        return updatedFactureRequestDto;
    }

    @Override
    public List<FactureRequestDto> findAllFactures() {
        List<FactureRequestDto> factures = factureRepository.findAll()
                .stream()
                .map(FactureRequestDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction("Liste de toutes les factures récupérée.");

        return factures;
    }

    @Override
    public List<FactureRequestDto> findFacturesByStatut(StatutPaiement statutPaiement) {
        List<FactureRequestDto> factures = factureRepository.findFacturesByStatutPaiement(statutPaiement)
                .stream()
                .map(FactureRequestDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction("Factures récupérées avec le statut : " + statutPaiement);

        return factures;
    }

    @Override
    public List<FactureRequestDto> findFacturesByModePaiement(ModePaiement modePaiement) {
        List<FactureRequestDto> factures = factureRepository.findFacturesByModePaiement(modePaiement)
                .stream()
                .map(FactureRequestDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction("Factures récupérées avec le mode de paiement : " + modePaiement);

        return factures;
    }

    @Override
    public FactureRequestDto findById(Integer FactureId) {
        FactureRequestDto factureRequestDto = factureRepository.findById(FactureId)
                .map(FactureRequestDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Facture avec l'ID : " + FactureId + " non trouvée."));

        historiqueActionService.enregistrerAction("Facture ID " + FactureId + " récupérée.");

        return factureRequestDto;
    }

    @Override
    public void deleteFacture(Integer FactureId) {
        Facture factureToDelete = factureRepository.findById(FactureId)
                .orElseThrow(() -> new EntityNotFoundException("La facture avec l'ID : " + FactureId + " n'existe pas et ne peut pas être supprimée."));



        factureRepository.deleteById(FactureId);

        historiqueActionService.enregistrerAction("Facture ID " + FactureId + " supprimée.");
    }

    @Override
    public PatientRequestDto findPatientByFactureId(Integer factureId) {
        Facture facture = factureRepository.findById(factureId)
                .orElseThrow(() -> new EntityNotFoundException("Facture avec l'ID : " + factureId + " non trouvée."));

        PatientRequestDto patientRequestDto = Optional.ofNullable(facture.getPatient())
                .map(PatientRequestDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Aucun patient associé à la facture avec l'ID : " + factureId));

        historiqueActionService.enregistrerAction("Patient récupéré pour la facture ID : " + factureId);

        return patientRequestDto;
    }

    @Override
    @Transactional
    public FactureRequestDto createFactureForConsultation(Integer consultationId, FactureRequestDto factureRequestDto) {
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
        newFacture.setMontant(factureRequestDto.getMontant() != null ? factureRequestDto.getMontant().floatValue() : null);
        newFacture.setDateEmission(factureRequestDto.getDateEmission() != null ? factureRequestDto.getDateEmission() : LocalDate.now());
        newFacture.setStatutPaiement(factureRequestDto.getStatutPaiement() != null ? factureRequestDto.getStatutPaiement() : StatutPaiement.NONPAYE);
        newFacture.setModePaiement(factureRequestDto.getModePaiement() != null ? factureRequestDto.getModePaiement() : ModePaiement.ESPECE);


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

        return FactureRequestDto.fromEntity(createdFacture);
    }

    @Override
    public FactureRequestDto updateStatutPaiement(Integer factureId, StatutPaiement nouveauStatut) {
        Facture existingFacture = factureRepository.findById(factureId)
                .orElseThrow(() -> new EntityNotFoundException("La facture avec l'ID " + factureId + " n'existe pas."));

        existingFacture.setStatutPaiement(nouveauStatut);

        FactureRequestDto updatedFactureRequestDto = FactureRequestDto.fromEntity(
                factureRepository.save(existingFacture)
        );

        historiqueActionService.enregistrerAction(
                "Statut de paiement de la facture ID " + factureId + " mis à jour à : " + nouveauStatut
        );

        return updatedFactureRequestDto;
    }
}