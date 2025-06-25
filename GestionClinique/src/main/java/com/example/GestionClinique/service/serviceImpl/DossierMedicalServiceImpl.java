package com.example.GestionClinique.service.serviceImpl;

import com.example.GestionClinique.dto.ConsultationSummaryDto;
import com.example.GestionClinique.dto.DossierMedicalDto;
import com.example.GestionClinique.dto.PatientDto;
import com.example.GestionClinique.dto.PrescriptionDto;
import com.example.GestionClinique.model.entity.Consultation;
import com.example.GestionClinique.model.entity.DossierMedical;
import com.example.GestionClinique.model.entity.Patient;
import com.example.GestionClinique.model.entity.Prescription;
import com.example.GestionClinique.repository.ConsultationRepository;
import com.example.GestionClinique.repository.DossierMedicalRepository;
import com.example.GestionClinique.repository.PatientRepository;
import com.example.GestionClinique.repository.PrescriptionRepository;
import com.example.GestionClinique.service.DossierMedicalService;
import com.example.GestionClinique.service.HistoriqueActionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime; // Using LocalDateTime for creationDate (from abstract entity)
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DossierMedicalServiceImpl implements DossierMedicalService {
    private final DossierMedicalRepository dossierMedicalRepository;
    private final PatientRepository patientRepository;
    private final ConsultationRepository consultationRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final HistoriqueActionService historiqueActionService;

    @Autowired
    public DossierMedicalServiceImpl(DossierMedicalRepository dossierMedicalRepository,
                                     PatientRepository patientRepository,
                                     ConsultationRepository consultationRepository,
                                     PrescriptionRepository prescriptionRepository,
                                     HistoriqueActionService historiqueActionService) {
        this.dossierMedicalRepository = dossierMedicalRepository;
        this.patientRepository = patientRepository;
        this.consultationRepository = consultationRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.historiqueActionService = historiqueActionService;
    }



    @Override
    @Transactional
    public DossierMedicalDto createDossierMedicalForPatient(Integer patientId, DossierMedicalDto dossierMedicalDto) {
        if (dossierMedicalDto == null) {
            throw new IllegalArgumentException("Les données du dossier médical ne peuvent pas être nulles.");
        }

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient introuvable avec l'ID " + patientId));

        if (patient.getDossierMedical() != null) {
            throw new IllegalStateException("Le patient avec l'ID " + patientId + " possède déjà un dossier médical (ID: " + patient.getDossierMedical().getId() + ").");
        }

        DossierMedical newDossierMedical = new DossierMedical();
        // dateCreation is automatically handled by @CreationTimestamp in EntityAbstracte
        newDossierMedical.setGroupeSanguin(dossierMedicalDto.getGroupeSanguin());
        newDossierMedical.setAntecedentsMedicaux(dossierMedicalDto.getAntecedentsMedicaux());
        newDossierMedical.setAllergies(dossierMedicalDto.getAllergies());
        newDossierMedical.setTraitementsEnCours(dossierMedicalDto.getTraitementsEnCours());
        newDossierMedical.setObservations(dossierMedicalDto.getObservations());

        newDossierMedical.setPatient(patient);
        patient.setDossierMedical(newDossierMedical);

        DossierMedical savedDossier = dossierMedicalRepository.save(newDossierMedical);

        historiqueActionService.enregistrerAction(
                "Création du dossier médical ID: " + savedDossier.getId() +
                        " pour le patient ID: " + patientId +
                        " (Nom: " + patient.getInfoPersonnel().getNom() + " " + patient.getInfoPersonnel().getPrenom() + ")"
        );

        return DossierMedicalDto.fromEntity(savedDossier);
    }



    @Override
    @Transactional
    public DossierMedicalDto updateDossierMedical(Integer id, DossierMedicalDto dossierMedicalDto) {
        if (dossierMedicalDto == null) {
            throw new IllegalArgumentException("Les données de mise à jour du dossier médical ne peuvent pas être nulles.");
        }

        DossierMedical existingDossierMedical = dossierMedicalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dossier médical introuvable avec l'ID " + id));

        // Store Old Values for Logging (creationDate and modificationDate are automatic)
        String oldGroupeSanguin = existingDossierMedical.getGroupeSanguin();
        String oldAntecedents = existingDossierMedical.getAntecedentsMedicaux();
        String oldAllergies = existingDossierMedical.getAllergies();
        String oldTraitements = existingDossierMedical.getTraitementsEnCours();
        String oldObservations = existingDossierMedical.getObservations();

        // Update direct attributes (only if provided in DTO)
        if (dossierMedicalDto.getGroupeSanguin() != null && !dossierMedicalDto.getGroupeSanguin().trim().isEmpty()) {
            existingDossierMedical.setGroupeSanguin(dossierMedicalDto.getGroupeSanguin());
        }
        if (dossierMedicalDto.getAntecedentsMedicaux() != null) {
            existingDossierMedical.setAntecedentsMedicaux(dossierMedicalDto.getAntecedentsMedicaux());
        }
        if (dossierMedicalDto.getAllergies() != null) {
            existingDossierMedical.setAllergies(dossierMedicalDto.getAllergies());
        }
        if (dossierMedicalDto.getTraitementsEnCours() != null) {
            existingDossierMedical.setTraitementsEnCours(dossierMedicalDto.getTraitementsEnCours());
        }
        if (dossierMedicalDto.getObservations() != null) {
            existingDossierMedical.setObservations(dossierMedicalDto.getObservations());
        }

        // --- Handling Collections (Consultations and Prescriptions) ---
        if (dossierMedicalDto.getConsultationSummaries() != null) {
            List<Consultation> updatedConsultations = dossierMedicalDto.getConsultationSummaries().stream()
                    .map(summaryDto -> {
                        if (summaryDto.getId() == null) {
                            throw new IllegalArgumentException("Une consultation dans la liste de mise à jour n'a pas d'ID. Les nouvelles consultations doivent être créées via leur service dédié.");
                        }
                        Consultation consultation = consultationRepository.findById(summaryDto.getId())
                                .orElseThrow(() -> new EntityNotFoundException("Consultation introuvable avec l'ID: " + summaryDto.getId()));
                        if (!Objects.equals(consultation.getDossierMedical(), existingDossierMedical)) {
                            consultation.setDossierMedical(existingDossierMedical);
                            consultationRepository.save(consultation);
                        }
                        return consultation;
                    })
                    .collect(Collectors.toList());

            existingDossierMedical.getConsultations().clear();
            existingDossierMedical.getConsultations().addAll(updatedConsultations);
        }

        if (dossierMedicalDto.getPrescriptions() != null) {
            List<Prescription> updatedPrescriptions = dossierMedicalDto.getPrescriptions().stream()
                    .map(prescriptionDto -> {
                        if (prescriptionDto.getId() == null) {
                            throw new IllegalArgumentException("Une prescription dans la liste de mise à jour n'a pas d'ID. Les nouvelles prescriptions doivent être créées via leur service dédié.");
                        }
                        Prescription prescription = prescriptionRepository.findById(prescriptionDto.getId())
                                .orElseThrow(() -> new EntityNotFoundException("Prescription introuvable avec l'ID: " + prescriptionDto.getId()));
                        if (!Objects.equals(prescription.getDossierMedical(), existingDossierMedical)) {
                            prescription.setDossierMedical(existingDossierMedical);
                            prescriptionRepository.save(prescription);
                        }
                        return prescription;
                    })
                    .collect(Collectors.toList());

            existingDossierMedical.getPrescriptions().clear();
            existingDossierMedical.getPrescriptions().addAll(updatedPrescriptions);
        }

        DossierMedicalDto updatedDossier = DossierMedicalDto.fromEntity(
                dossierMedicalRepository.save(existingDossierMedical)
        );

        // --- Historique Logging (updated to reflect new fields, removed numeroDossier and explicit dateCreation) ---
        StringBuilder logMessage = new StringBuilder("Mise à jour du dossier médical ID: " + id);
        if (existingDossierMedical.getPatient() != null) {
            logMessage.append(" pour le patient ID: ").append(existingDossierMedical.getPatient().getId());
            logMessage.append(" (Nom: ").append(existingDossierMedical.getPatient().getInfoPersonnel().getNom()).append(" ").append(existingDossierMedical.getPatient().getInfoPersonnel().getPrenom()).append(")");
        }
        // Removed oldDateCreation check as creationDate is automatically managed
        if (!Objects.equals(oldGroupeSanguin, updatedDossier.getGroupeSanguin())) logMessage.append(". Groupe Sanguin: '").append(oldGroupeSanguin).append("' -> '").append(updatedDossier.getGroupeSanguin()).append("'");
        if (!Objects.equals(oldAntecedents, updatedDossier.getAntecedentsMedicaux())) logMessage.append(". Antécédents: '").append(oldAntecedents).append("' -> '").append(updatedDossier.getAntecedentsMedicaux()).append("'");
        if (!Objects.equals(oldAllergies, updatedDossier.getAllergies())) logMessage.append(". Allergies: '").append(oldAllergies).append("' -> '").append(updatedDossier.getAllergies()).append("'");
        if (!Objects.equals(oldTraitements, updatedDossier.getTraitementsEnCours())) logMessage.append(". Traitements: '").append(oldTraitements).append("' -> '").append(updatedDossier.getTraitementsEnCours()).append("'");
        if (!Objects.equals(oldObservations, updatedDossier.getObservations())) logMessage.append(". Observations: '").append(oldObservations).append("' -> '").append(updatedDossier.getObservations()).append("'");

        historiqueActionService.enregistrerAction(logMessage.toString());
        return updatedDossier;
    }



    @Override
    @Transactional()
    public DossierMedicalDto findDossierMedicalById(Integer id) {
        DossierMedical foundDossierMedical = dossierMedicalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Le Dossier médical avec l'ID " + id + " n'existe pas."));

        historiqueActionService.enregistrerAction(
                "Recherche du dossier médical ID: " + id
        );

        return DossierMedicalDto.fromEntity(foundDossierMedical);
    }



    @Override
    @Transactional()
    public List<DossierMedicalDto> findAllDossierMedical() {
        List<DossierMedicalDto> allDossiers = dossierMedicalRepository.findAll()
                .stream()
                .map(DossierMedicalDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction(
                "Affichage de tous les dossiers médicaux (nombre de résultats: " + allDossiers.size() + ")"
        );

        return allDossiers;
    }



    @Override
    @Transactional()
    public PatientDto findPatientByDossierMedicalId(Integer dossierMedicalId) {
        DossierMedical dossierMedical = dossierMedicalRepository.findById(dossierMedicalId)
                .orElseThrow(() -> new EntityNotFoundException("Dossier médical introuvable avec l'ID " + dossierMedicalId));

        PatientDto patientDto = Optional.ofNullable(dossierMedical.getPatient())
                .map(PatientDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Aucun patient associé au dossier médical avec l'ID " + dossierMedicalId));

        historiqueActionService.enregistrerAction(
                "Recherche du patient associé au dossier médical ID: " + dossierMedicalId +
                        " (Nom du patient: " + patientDto.getInfoPersonnel().getNom() + " " + patientDto.getInfoPersonnel().getPrenom() + ")"
        );

        return patientDto;
    }



    @Override
    @Transactional
    public void deleteDossierMedicalById(Integer id) {
        DossierMedical dossierMedicalToDelete = dossierMedicalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Le Dossier médical avec l'ID : " + id + " n'existe pas et ne peut pas être supprimé."));

        Patient patient = dossierMedicalToDelete.getPatient();
        if (patient != null) {
            patient.setDossierMedical(null);
            patientRepository.save(patient);
        }

        if (dossierMedicalToDelete.getConsultations() != null && !dossierMedicalToDelete.getConsultations().isEmpty()) {
            for (Consultation consultation : dossierMedicalToDelete.getConsultations()) {
                consultation.setDossierMedical(null);
                consultationRepository.save(consultation);
            }
            dossierMedicalToDelete.getConsultations().clear();
        }

        if (dossierMedicalToDelete.getPrescriptions() != null && !dossierMedicalToDelete.getPrescriptions().isEmpty()) {
            for (Prescription prescription : dossierMedicalToDelete.getPrescriptions()) {
                prescription.setDossierMedical(null);
                prescriptionRepository.save(prescription);
            }
            dossierMedicalToDelete.getPrescriptions().clear();
        }

        dossierMedicalRepository.deleteById(id);

        historiqueActionService.enregistrerAction(
                "Suppression du dossier médical ID: " + id +
                        " (Date Création: " + (dossierMedicalToDelete.getCreationDate() != null ? dossierMedicalToDelete.getCreationDate() : "N/A") + // Using getCreationDate
                        ", Patient ID: " + (patient != null ? patient.getId() : "N/A") + ")"
        );
    }



    @Override
    @Transactional()
    public DossierMedicalDto findDossierMedicalByPatientId(Integer patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient introuvable avec l'ID: " + patientId));

        DossierMedical dossierMedical = Optional.ofNullable(patient.getDossierMedical())
                .orElseThrow(() -> new EntityNotFoundException("Dossier médical introuvable pour le patient ID: " + patientId + ". Ce patient n'a pas encore de dossier médical."));

        historiqueActionService.enregistrerAction(
                "Recherche du dossier médical pour le patient ID: " + patientId +
                        " (Date Création: " + dossierMedical.getCreationDate() + ")" // Using getCreationDate
        );
        return DossierMedicalDto.fromEntity(dossierMedical);
    }
}