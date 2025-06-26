package com.example.GestionClinique.service.serviceImpl;

import com.example.GestionClinique.dto.ConsultationSummaryDto; // Import if not already
import com.example.GestionClinique.dto.PatientSummaryDto; // Import if not already
import com.example.GestionClinique.dto.PrescriptionDto;
import com.example.GestionClinique.dto.UtilisateurSummaryDto; // Import if not already
import com.example.GestionClinique.model.entity.Consultation;
import com.example.GestionClinique.model.entity.DossierMedical; // Needed for linking to Patient via DossierMedical
import com.example.GestionClinique.model.entity.Patient;
import com.example.GestionClinique.model.entity.Prescription;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.repository.ConsultationRepository;
import com.example.GestionClinique.repository.PatientRepository;
import com.example.GestionClinique.repository.PrescriptionRepository;
import com.example.GestionClinique.repository.UtilisateurRepository;
import com.example.GestionClinique.service.HistoriqueActionService;
import com.example.GestionClinique.service.PrescriptionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final ConsultationRepository consultationRepository;
    private final UtilisateurRepository utilisateurRepository; // For Medecin
    private final PatientRepository patientRepository;
    private final HistoriqueActionService historiqueActionService;

    @Autowired
    public PrescriptionServiceImpl(PrescriptionRepository prescriptionRepository,
                                   ConsultationRepository consultationRepository,
                                   UtilisateurRepository utilisateurRepository,
                                   PatientRepository patientRepository,
                                   HistoriqueActionService historiqueActionService) {
        this.prescriptionRepository = prescriptionRepository;
        this.consultationRepository = consultationRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.patientRepository = patientRepository;
        this.historiqueActionService = historiqueActionService;
    }


    @Override
    @Transactional
    public PrescriptionDto createPrescription(PrescriptionDto prescriptionDto) {
        // 1. Validate mandatory associations from DTO
        if (prescriptionDto.getConsultationId() == null ) {
            throw new IllegalArgumentException("Une prescription doit être associée à une consultation existante (ID de consultation manquant).");
        }

        // 2. Fetch the associated Consultation entity
        Consultation consultation = consultationRepository.findById(prescriptionDto.getConsultationId())
                .orElseThrow(() -> new EntityNotFoundException("La consultation avec l'ID " + prescriptionDto.getConsultationId() + " n'existe pas."));

        // 3. Create a new Prescription entity and set its direct properties from the DTO
        Prescription newPrescription = new Prescription();
        newPrescription.setTypePrescription(prescriptionDto.getTypePrescription());
        newPrescription.setMedicaments(prescriptionDto.getMedicaments());
        newPrescription.setInstructions(prescriptionDto.getInstructions());
        newPrescription.setDureePrescription(prescriptionDto.getDureePrescription());
        newPrescription.setQuantite(prescriptionDto.getQuantite());
        newPrescription.setDatePrescription(prescriptionDto.getDatePrescription() != null ? prescriptionDto.getDatePrescription() : LocalDate.now());

        // 4. Set the associated Consultation, Medecin, Patient, and DossierMedical
        newPrescription.setConsultation(consultation);

        // Link Medecin from the Consultation. Assuming Consultation's Medecin is set correctly.
        if (consultation.getMedecin() != null) {
            newPrescription.setMedecin(consultation.getMedecin());
        } else {
            // This indicates a data integrity issue if a Consultation exists without a Medecin
            throw new IllegalStateException("Le médecin n'est pas défini pour la consultation associée (ID: " + consultation.getId() + ").");
        }

        // Link Patient and DossierMedical from the Consultation's DossierMedical
        if (consultation.getDossierMedical() != null && consultation.getDossierMedical().getPatient() != null) {
            newPrescription.setPatient(consultation.getDossierMedical().getPatient());
            newPrescription.setDossierMedical(consultation.getDossierMedical());
        } else {
            // This indicates a data integrity issue if a Consultation exists without a patient or dossier medical
            throw new IllegalStateException("Le patient ou son dossier médical n'est pas défini pour la consultation associée (ID: " + consultation.getId() + ").");
        }

        // 5. Save the new prescription
        Prescription savedPrescriptionEntity = prescriptionRepository.save(newPrescription);

        // 6. Update the Consultation to include this new prescription in its collection
        // This is important for bidirectional relationships and cascade settings.
        if (consultation.getPrescriptions() == null) {
            consultation.setPrescriptions(new java.util.ArrayList<>());
        }
        consultation.getPrescriptions().add(savedPrescriptionEntity);
        consultationRepository.save(consultation); // Persist the updated consultation

        PrescriptionDto savedPrescriptionDto = PrescriptionDto.fromEntity(savedPrescriptionEntity);

        historiqueActionService.enregistrerAction(
                "Création de la prescription ID: " + savedPrescriptionDto.getId() + " pour la consultation ID: " + consultation.getId() +
                        " (Médecin: " + (newPrescription.getMedecin() != null && newPrescription.getMedecin().getInfoPersonnel() != null ? newPrescription.getMedecin().getInfoPersonnel().getNom() + " " + newPrescription.getMedecin().getInfoPersonnel().getPrenom() : "N/A") +
                        ", Patient: " + (newPrescription.getPatient() != null && newPrescription.getPatient().getInfoPersonnel() != null ? newPrescription.getPatient().getInfoPersonnel().getNom() + " " + newPrescription.getPatient().getInfoPersonnel().getPrenom() : "N/A") + ")"
        );

        return savedPrescriptionDto;
    }



    @Override
    @Transactional
    public PrescriptionDto updatePrescription(Integer prescriptionId, PrescriptionDto prescriptionDto) {
        Prescription existingPrescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new EntityNotFoundException("Prescription avec l'ID " + prescriptionId + " n'existe pas."));

        // Update direct properties from DTO
        existingPrescription.setTypePrescription(prescriptionDto.getTypePrescription());
        existingPrescription.setMedicaments(prescriptionDto.getMedicaments());
        existingPrescription.setInstructions(prescriptionDto.getInstructions());
        existingPrescription.setDureePrescription(prescriptionDto.getDureePrescription());
        existingPrescription.setQuantite(prescriptionDto.getQuantite());
        existingPrescription.setDatePrescription(prescriptionDto.getDatePrescription() != null ? prescriptionDto.getDatePrescription() : existingPrescription.getDatePrescription());

        // Handle updates to associated entities if provided in DTO (e.g., changing consultation, patient, or medecin)
        // This is generally less common for an existing prescription; usually, a new one is created.
        // However, if the business logic allows, you'd fetch the new entities by ID from summary DTOs:

        if (prescriptionDto.getConsultationId() != null) {
            Consultation newConsultation = consultationRepository.findById(prescriptionDto.getConsultationId())
                    .orElseThrow(() -> new EntityNotFoundException("La nouvelle consultation associée avec l'ID " + prescriptionDto.getConsultationId() + " n'existe pas."));
            existingPrescription.setConsultation(newConsultation);
        }
        // Similar logic for PatientSummary and UtilisateurSummary (Medecin) if they can be changed.
        // Be mindful of data integrity if you allow changing these.

        Prescription updatedPrescriptionEntity = prescriptionRepository.save(existingPrescription);
        PrescriptionDto updatedPrescriptionDto = PrescriptionDto.fromEntity(updatedPrescriptionEntity);

        historiqueActionService.enregistrerAction(
                "Mise à jour de la prescription ID: " + prescriptionId +
                        " (Consultation ID: " + (updatedPrescriptionEntity.getConsultation() != null ? updatedPrescriptionEntity.getConsultation().getId() : "N/A") + ")"
        );

        return updatedPrescriptionDto;
    }



    @Override
    @Transactional
    public PrescriptionDto findById(Integer prescriptionId) {
        Prescription foundPrescriptionEntity = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new EntityNotFoundException("La prescription avec l'ID " + prescriptionId + " n'existe pas."));

        historiqueActionService.enregistrerAction(
                "Recherche de la prescription ID: " + prescriptionId
        );

        return PrescriptionDto.fromEntity(foundPrescriptionEntity);
    }



    @Override
    @Transactional
    public List<PrescriptionDto> findAllPrescription() {
        List<PrescriptionDto> allPrescriptions = prescriptionRepository.findAll()
                .stream()
                .map(PrescriptionDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction("Affichage de toutes les prescriptions.");

        return allPrescriptions;
    }



    @Override
    @Transactional
    public void deletePrescription(Integer prescriptionId) {
        Prescription prescriptionToDelete = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new EntityNotFoundException("La prescription avec l'ID " + prescriptionId + " n'existe pas et ne peut être supprimée."));

        // IMPORTANT: Handle bidirectional relationship with Consultation if necessary.
        // If Consultation holds a collection of Prescriptions, ensure to remove this prescription from that collection.
        Consultation associatedConsultation = prescriptionToDelete.getConsultation();
        if (associatedConsultation != null) {
            associatedConsultation.getPrescriptions().remove(prescriptionToDelete);
            consultationRepository.save(associatedConsultation); // Persist the change on the Consultation side
        }

        prescriptionRepository.deleteById(prescriptionId);

        historiqueActionService.enregistrerAction(
                "Suppression de la prescription ID: " + prescriptionId
        );
    }



    @Override
    @Transactional
    public List<PrescriptionDto> findPrescriptionByMedecinId(Integer utilisateurId) {
        Utilisateur existingMedecin = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Le médecin (utilisateur) avec l'ID " + utilisateurId + " n'existe pas."));

        // Changed from getHistoriqueActions() to getPrescriptions() as in your original code.
        // This assumes your Utilisateur entity has a @OneToMany(mappedBy = "medecin") List<Prescription> prescriptions;
        if (existingMedecin.getPrescriptions() == null) {
            return List.of(); // Return empty list if collection is null
        }

        List<PrescriptionDto> prescriptions = existingMedecin.getPrescriptions()
                .stream()
                .filter(Objects::nonNull) // Filter out any potential nulls in the collection
                .map(PrescriptionDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction(
                "Recherche des prescriptions par médecin ID: " + utilisateurId
        );

        return prescriptions;
    }



    @Override
    @Transactional
    public List<PrescriptionDto> findPrescriptionByPatientId(Integer patientId) {
        Patient existingPatient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Le patient avec l'ID " + patientId + " n'existe pas."));

        // This assumes your Patient entity has a @OneToMany(mappedBy = "patient") List<Prescription> prescriptions;
        if (existingPatient.getPrescriptions() == null) {
            return List.of(); // Return empty list if collection is null
        }

        List<PrescriptionDto> prescriptions = existingPatient.getPrescriptions()
                .stream()
                .filter(Objects::nonNull) // Filter out any potential nulls in the collection
                .map(PrescriptionDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction(
                "Recherche des prescriptions par patient ID: " + patientId
        );

        return prescriptions;
    }



    @Override
    @Transactional
    public List<PrescriptionDto> findPrescriptionByConsultationId(Integer consultationId) {
        Consultation existingConsultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new EntityNotFoundException("La consultation avec l'ID " + consultationId + " n'existe pas."));

        // This assumes your Consultation entity has a @OneToMany(mappedBy = "consultation") List<Prescription> prescriptions;
        if (existingConsultation.getPrescriptions() == null) {
            return List.of(); // Return empty list if collection is null
        }

        List<PrescriptionDto> prescriptions = existingConsultation.getPrescriptions()
                .stream()
                .filter(Objects::nonNull) // Filter out any potential nulls in the collection
                .map(PrescriptionDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction(
                "Recherche des prescriptions par consultation ID: " + consultationId
        );

        return prescriptions;
    }
}