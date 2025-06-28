package com.example.GestionClinique.service.serviceImpl;

import com.example.GestionClinique.dto.RequestDto.ConsultationRequestDto;
import com.example.GestionClinique.dto.RequestDto.DossierMedicalRequestDto;
import com.example.GestionClinique.dto.RequestDto.PrescriptionRequestDto;
import com.example.GestionClinique.dto.RequestDto.RendezVousRequestDto;
import com.example.GestionClinique.model.entity.*;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import com.example.GestionClinique.repository.*;
import com.example.GestionClinique.service.ConsultationService;
import com.example.GestionClinique.service.HistoriqueActionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConsultationServiceImpl implements ConsultationService {
    private final ConsultationRepository consultationRepository;
    private final RendezVousRepository rendezVousRepository;
    private final DossierMedicalRepository dossierMedicalRepository;
    private final PrescriptionRepository prescriptionRepository; // Still needed for addPrescriptionToConsultation
    // private final HistoriqueActionRepository historiqueActionRepository; // Removed, as HistoriqueActionService is used
    private final UtilisateurRepository utilisateurRepository; // Needed for Medecin
    private final HistoriqueActionService historiqueActionService;


    @Autowired
    public ConsultationServiceImpl(ConsultationRepository consultationRepository,
                                   RendezVousRepository rendezVousRepository,
                                   DossierMedicalRepository dossierMedicalRepository,
                                   PrescriptionRepository prescriptionRepository,
                                   // HistoriqueActionRepository historiqueActionRepository, // Removed from constructor
                                   UtilisateurRepository utilisateurRepository,
                                   HistoriqueActionService historiqueActionService) {
        this.consultationRepository = consultationRepository;
        this.rendezVousRepository = rendezVousRepository;
        this.dossierMedicalRepository = dossierMedicalRepository;
        this.prescriptionRepository = prescriptionRepository;
        // this.historiqueActionRepository = historiqueActionRepository; // Removed
        this.utilisateurRepository = utilisateurRepository;
        this.historiqueActionService = historiqueActionService;
    }


    @Override
    @Transactional
    public ConsultationRequestDto createConsultation(ConsultationRequestDto consultationRequestDto) {
        Consultation consultation = new Consultation();
        // Set direct attributes from DTO to entity
        consultation.setMotifs(consultationRequestDto.getMotifs());
        consultation.setTensionArterielle(consultationRequestDto.getTensionArterielle());
        consultation.setTemperature(consultationRequestDto.getTemperature());
        consultation.setPoids(consultationRequestDto.getPoids());
        consultation.setTaille(consultationRequestDto.getTaille());
        consultation.setCompteRendu(consultationRequestDto.getCompteRendu());
        consultation.setDiagnostic(consultationRequestDto.getDiagnostic());


        // Handle relationships by fetching entities using IDs from Summary DTOs
        if (consultationRequestDto.getRendezVous() != null && consultationRequestDto.getRendezVous().getId() != null) {
            RendezVous rendezVous = rendezVousRepository.findById(consultationRequestDto.getRendezVous().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Rendez-vous associé introuvable avec l'ID: " + consultationRequestDto.getRendezVous().getId()));
            consultation.setRendezVous(rendezVous);
        }

        if (consultationRequestDto.getDossierMedical() != null && consultationRequestDto.getDossierMedical().getId() != null) {
            DossierMedical dossierMedical = dossierMedicalRepository.findById(consultationRequestDto.getDossierMedical().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Dossier médical associé introuvable avec l'ID: " + consultationRequestDto.getDossierMedical().getId()));
            consultation.setDossierMedical(dossierMedical);
        }

        // If a Medecin (Utilisateur) is provided in the DTO's summary, fetch and set it
        if (consultationRequestDto.getMedecin() != null && consultationRequestDto.getMedecin().getId() != null) {
            Utilisateur medecin = utilisateurRepository.findById(consultationRequestDto.getMedecin().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Médecin associé introuvable avec l'ID: " + consultationRequestDto.getMedecin().getId()));
            consultation.setMedecin(medecin);
        } else {
            // Potentially derive the médecin from the RendezVous if it's set
            if (consultation.getRendezVous() != null && consultation.getRendezVous().getMedecin() != null) {
                consultation.setMedecin(consultation.getRendezVous().getMedecin());
            }
        }


        Consultation savedConsultation = consultationRepository.save(consultation);

        // Update the associated RendezVous if any, to link it to this consultation
        if (savedConsultation.getRendezVous() != null) {
            RendezVous associatedRendezVous = savedConsultation.getRendezVous();
            associatedRendezVous.setConsultation(savedConsultation); // Link the consultation back to the rendezvous
            // Optionally, change the status of the rendezvous if it's now "started" or similar
            // associatedRendezVous.setStatut(StatutRDV.EN_COURS); // Example
            rendezVousRepository.save(associatedRendezVous);
        }


        historiqueActionService.enregistrerAction(
                "Création d'une consultation (ID consultation: " + savedConsultation.getId() +
                        ", ID Rendez-vous: " + (savedConsultation.getRendezVous() != null ? savedConsultation.getRendezVous().getId() : "N/A") + ")"
        );

        return ConsultationRequestDto.fromEntity(savedConsultation);
    }

    @Override
    @Transactional
    public ConsultationRequestDto updateConsultation(Integer id, ConsultationRequestDto consultationRequestDto) {
        Consultation existingConsultation = consultationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("La consultation avec l'ID " + id + " n'existe pas."));

        // Update direct attributes
        existingConsultation.setMotifs(consultationRequestDto.getMotifs());
        existingConsultation.setTensionArterielle(consultationRequestDto.getTensionArterielle());
        existingConsultation.setTemperature(consultationRequestDto.getTemperature());
        existingConsultation.setPoids(consultationRequestDto.getPoids());
        existingConsultation.setTaille(consultationRequestDto.getTaille());
        existingConsultation.setCompteRendu(consultationRequestDto.getCompteRendu());
        existingConsultation.setDiagnostic(consultationRequestDto.getDiagnostic());

        // Handle updates for related entities via their IDs
        // For DossierMedical, you usually update the association or create if not present.
        // If the DTO provides a DossierMedicalSummary, update the association
        if (consultationRequestDto.getDossierMedical() != null && consultationRequestDto.getDossierMedical().getId() != null) {
            DossierMedical dossierMedical = dossierMedicalRepository.findById(consultationRequestDto.getDossierMedical().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Dossier médical associé introuvable avec l'ID: " + consultationRequestDto.getDossierMedical().getId()));
            existingConsultation.setDossierMedical(dossierMedical);
        } else if (consultationRequestDto.getDossierMedical() != null && consultationRequestDto.getDossierMedical().getId() == null) {
            // Case where dossier medical is explicitly set to null or disassociated
            existingConsultation.setDossierMedical(null);
        }

        // For RendezVous, generally a consultation is linked to ONE RendezVous at creation.
        // Updating it here would mean changing the linked rendez-vous, which might be a complex business rule.
        // It's often better to handle rendezvous association at creation (startConsultation) or disallow changing it.
        // If you need to allow changing the associated rendez-vous, implement similar logic as DossierMedical.
        if (consultationRequestDto.getRendezVous() != null && consultationRequestDto.getRendezVous().getId() != null) {
            RendezVous rendezVous = rendezVousRepository.findById(consultationRequestDto.getRendezVous().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Rendez-vous associé introuvable avec l'ID: " + consultationRequestDto.getRendezVous().getId()));
            existingConsultation.setRendezVous(rendezVous);
        } else if (consultationRequestDto.getRendezVous() != null && consultationRequestDto.getRendezVous().getId() == null) {
            existingConsultation.setRendezVous(null);
        }

        // Handle Medecin update (if allowed)
        if (consultationRequestDto.getMedecin() != null && consultationRequestDto.getMedecin().getId() != null) {
            Utilisateur medecin = utilisateurRepository.findById(consultationRequestDto.getMedecin().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Médecin associé introuvable avec l'ID: " + consultationRequestDto.getMedecin().getId()));
            existingConsultation.setMedecin(medecin);
        } else if (consultationRequestDto.getMedecin() != null && consultationRequestDto.getMedecin().getId() == null) {
            existingConsultation.setMedecin(null);
        }


        Consultation updatedConsultation = consultationRepository.save(existingConsultation);

        historiqueActionService.enregistrerAction(
                "Mise à jour de la consultation ID: " + id
        );

        return ConsultationRequestDto.fromEntity(updatedConsultation);
    }


    @Override
    @Transactional
    public ConsultationRequestDto findById(Integer id) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("La consultation avec l'ID " + id + " n'existe pas."));

        historiqueActionService.enregistrerAction(
                "Consultation ID: " + id + " recherchée"
        );

        return ConsultationRequestDto.fromEntity(consultation);
    }

    @Override
    @Transactional
    public List<ConsultationRequestDto> findAll() {
        List<ConsultationRequestDto> allConsultations = consultationRepository.findAll().stream()
                .map(ConsultationRequestDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction(
                "Liste de toutes les consultations affichée"
        );

        return allConsultations;
    }

    @Override
    @Transactional
    public DossierMedicalRequestDto findDossierMedicalByConsultationId(Integer id) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consultation introuvable avec l'ID: " + id));

        DossierMedical dossierMedical = consultation.getDossierMedical();
        if (dossierMedical == null) {
            throw new EntityNotFoundException("Aucun dossier médical associé à la consultation ID: " + id);
        }

        historiqueActionService.enregistrerAction(
                "Dossier médical lié à la consultation ID: " + id + " affiché"
        );

        return DossierMedicalRequestDto.fromEntity(dossierMedical);
    }

    @Override
    @Transactional
    public RendezVousRequestDto findRendezVousByConsultationId(Integer id) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Consultation introuvable avec l'ID: " + id));

        RendezVous rendezVous = consultation.getRendezVous();
        if (rendezVous == null) {
            throw new EntityNotFoundException("Aucun rendez-vous associé à la consultation ID: " + id);
        }

        historiqueActionService.enregistrerAction(
                "Rendez-vous lié à la consultation ID: " + id + " affiché"
        );

        return RendezVousRequestDto.fromEntity(rendezVous);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        Consultation consultationToDelete = consultationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("La consultation avec l'ID " + id + " n'existe pas et ne peut pas être supprimée."));

        // Dissociate RendezVous if it exists to prevent potential foreign key constraints issues
        // or unexpected behavior. Note: This assumes RendezVous is the owning side or
        // you have cascade options set appropriately on Consultation.
        if (consultationToDelete.getRendezVous() != null) {
            RendezVous rendezVousAssocie = consultationToDelete.getRendezVous();
            rendezVousAssocie.setConsultation(null); // Break the link
            rendezVousRepository.save(rendezVousAssocie);
        }

        // Handle prescriptions associated with this consultation
        if (consultationToDelete.getPrescriptions() != null) {
            // Option 1: Delete associated prescriptions (if they are tightly coupled)
            // prescriptionRepository.deleteAll(consultationToDelete.getPrescriptions());

            // Option 2: Dissociate prescriptions (if they can exist independently)
            for (Prescription p : consultationToDelete.getPrescriptions()) {
                p.setConsultation(null); // Break the link
                // p.setDossierMedical(null); // If also linked to DM, might need to handle this
                // p.setPatient(null); // Similar
                // p.setMedecin(null); // Similar
                prescriptionRepository.save(p);
            }
            consultationToDelete.getPrescriptions().clear(); // Clear the collection in the entity
        }


        consultationRepository.deleteById(id);

        historiqueActionService.enregistrerAction(
                "Suppression de la consultation ID: " + id
        );
    }

    @Override
    @Transactional
    public ConsultationRequestDto startConsultation(Integer rendezVousId, ConsultationRequestDto consultationDetails) {
        RendezVous rendezVous = rendezVousRepository.findById(rendezVousId)
                .orElseThrow(() -> new EntityNotFoundException("Rendez-vous introuvable avec l'ID " + rendezVousId));

        if (rendezVous.getStatut() != StatutRDV.CONFIRME) {
            throw new IllegalStateException("Impossible de démarrer la consultation: le rendez-vous n'est pas CONFIRME.");
        }

        if (rendezVous.getConsultation() != null) {
            throw new IllegalStateException("Le rendez-vous avec l'ID " + rendezVousId + " est déjà lié à une consultation.");
        }

        // Get or create DossierMedical
        DossierMedical dossierMedical = Optional.ofNullable(rendezVous.getPatient().getDossierMedical())
                .orElseGet(() -> {
                    DossierMedical newDossier = new DossierMedical();
                    newDossier.setPatient(rendezVous.getPatient());
                    newDossier.setAntecedentsMedicaux("");
                    newDossier.setAllergies("");
                    newDossier.setTraitementsEnCours("");
                    newDossier.setObservations("");
                    return dossierMedicalRepository.save(newDossier);
                });

        Consultation newConsultation = new Consultation();
        // Transfer direct attributes from the provided DTO
        newConsultation.setMotifs(consultationDetails.getMotifs());
        newConsultation.setTensionArterielle(consultationDetails.getTensionArterielle());
        newConsultation.setTemperature(consultationDetails.getTemperature());
        newConsultation.setPoids(consultationDetails.getPoids());
        newConsultation.setTaille(consultationDetails.getTaille());
        newConsultation.setCompteRendu(consultationDetails.getCompteRendu());
        newConsultation.setDiagnostic(consultationDetails.getDiagnostic());



        // Establish relationships
        newConsultation.setRendezVous(rendezVous);
        newConsultation.setDossierMedical(dossierMedical);
        newConsultation.setMedecin(rendezVous.getMedecin()); // Medecin comes from RendezVous

        Consultation savedConsultation = consultationRepository.save(newConsultation);

        // Update the RendezVous status and link it to the new Consultation
        rendezVous.setStatut(StatutRDV.TERMINE); // Or EN_COURS if the consultation is still ongoing
        rendezVous.setConsultation(savedConsultation);
        rendezVousRepository.save(rendezVous);

        historiqueActionService.enregistrerAction(
                "Lancement de la consultation ID: " + savedConsultation.getId() + " pour le rendez-vous ID: " + rendezVousId
        );

        return ConsultationRequestDto.fromEntity(savedConsultation);
    }

    @Override
    @Transactional
    public ConsultationRequestDto addPrescriptionToConsultation(Integer consultationId, PrescriptionRequestDto prescriptionRequestDto) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new EntityNotFoundException("Consultation introuvable avec l'ID " + consultationId));

        Prescription newPrescription = new Prescription();
        // Set direct attributes for the new Prescription
        newPrescription.setDatePrescription(prescriptionRequestDto.getDatePrescription() != null ? prescriptionRequestDto.getDatePrescription() : LocalDate.now());
        newPrescription.setTypePrescription(prescriptionRequestDto.getTypePrescription());
        newPrescription.setMedicaments(prescriptionRequestDto.getMedicaments());
        newPrescription.setInstructions(prescriptionRequestDto.getInstructions());
        newPrescription.setDureePrescription(prescriptionRequestDto.getDureePrescription());
        newPrescription.setQuantite(prescriptionRequestDto.getQuantite());


        // Establish relationships for the Prescription
        newPrescription.setConsultation(consultation);
        newPrescription.setPatient(consultation.getDossierMedical().getPatient()); // Get patient from consultation's dossier
        newPrescription.setMedecin(consultation.getMedecin()); // Get medecin from consultation

        // Persist the new prescription first
        Prescription savedPrescription = prescriptionRepository.save(newPrescription);


        // Add the saved prescription to the consultation's list (if it's the owning side or mapped by)
        if (consultation.getPrescriptions() == null) {
            consultation.setPrescriptions(new java.util.ArrayList<>());
        }
        consultation.getPrescriptions().add(savedPrescription);
        // Important: if Consultation is NOT the owning side of the relationship (e.g., Prescription owns the FK to Consultation),
        // you might not need to save the 'consultation' explicitly here, but saving the 'newPrescription' is critical.
        // However, if the relationship in Consultation is managed (e.g., CascadeType.ALL or orphanRemoval),
        // then saving Consultation will also save the new Prescription. It's safer to save the child entity first.
        consultationRepository.save(consultation); // Persist changes to consultation's prescription list


        historiqueActionService.enregistrerAction(
                "Ajout d'une prescription (ID: " + savedPrescription.getId() + ") à la consultation ID: " + consultationId
        );

        return ConsultationRequestDto.fromEntity(consultationRepository.findById(consultationId).orElseThrow()); // Re-fetch to ensure all relationships are fresh
    }

    @Override
    @Transactional
    public List<PrescriptionRequestDto> findPrescriptionsByConsultationId(Integer consultationId) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new EntityNotFoundException("Consultation introuvable avec l'ID " + consultationId));

        // Ensure the prescriptions collection is eagerly loaded or accessed within the transaction
        if (consultation.getPrescriptions() == null || consultation.getPrescriptions().isEmpty()) {
            return List.of(); // Return empty list if no prescriptions
        }

        historiqueActionService.enregistrerAction(
                "Affichage des prescriptions pour la consultation ID: " + consultationId
        );

        return consultation.getPrescriptions().stream()
                .filter(Objects::nonNull)
                .map(PrescriptionRequestDto::fromEntity)
                .collect(Collectors.toList());
    }
}