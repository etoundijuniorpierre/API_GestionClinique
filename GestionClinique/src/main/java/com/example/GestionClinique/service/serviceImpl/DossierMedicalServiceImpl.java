package com.example.GestionClinique.service.serviceImpl;

import com.example.GestionClinique.dto.RequestDto.DossierMedicalRequestDto;
import com.example.GestionClinique.dto.RequestDto.PatientRequestDto;
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
    public DossierMedicalRequestDto createDossierMedicalForPatient(Integer patientId, DossierMedicalRequestDto dossierMedicalRequestDto) {
        if (dossierMedicalRequestDto == null) {
            throw new IllegalArgumentException("Les données du dossier médical ne peuvent pas être nulles.");
        }

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient introuvable avec l'ID " + patientId));

        if (patient.getDossierMedical() != null) {
            throw new IllegalStateException("Le patient avec l'ID " + patientId + " possède déjà un dossier médical (ID: " + patient.getDossierMedical().getId() + ").");
        }

        DossierMedical newDossierMedical = new DossierMedical();
        // dateCreation is automatically handled by @CreationTimestamp in EntityAbstracte
        newDossierMedical.setGroupeSanguin(dossierMedicalRequestDto.getGroupeSanguin());
        newDossierMedical.setAntecedentsMedicaux(dossierMedicalRequestDto.getAntecedentsMedicaux());
        newDossierMedical.setAllergies(dossierMedicalRequestDto.getAllergies());
        newDossierMedical.setTraitementsEnCours(dossierMedicalRequestDto.getTraitementsEnCours());
        newDossierMedical.setObservations(dossierMedicalRequestDto.getObservations());

        newDossierMedical.setPatient(patient);
        patient.setDossierMedical(newDossierMedical);

        DossierMedical savedDossier = dossierMedicalRepository.save(newDossierMedical);

        historiqueActionService.enregistrerAction(
                "Création du dossier médical ID: " + savedDossier.getId() +
                        " pour le patient ID: " + patientId +
                        " (Nom: " + patient.getInfoPersonnel().getNom() + " " + patient.getInfoPersonnel().getPrenom() + ")"
        );

        return DossierMedicalRequestDto.fromEntity(savedDossier);
    }



    @Override
    @Transactional
    public DossierMedicalRequestDto updateDossierMedical(Integer id, DossierMedicalRequestDto dossierMedicalRequestDto) {
        if (dossierMedicalRequestDto == null) {
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
        if (dossierMedicalRequestDto.getGroupeSanguin() != null && !dossierMedicalRequestDto.getGroupeSanguin().trim().isEmpty()) {
            existingDossierMedical.setGroupeSanguin(dossierMedicalRequestDto.getGroupeSanguin());
        }
        if (dossierMedicalRequestDto.getAntecedentsMedicaux() != null) {
            existingDossierMedical.setAntecedentsMedicaux(dossierMedicalRequestDto.getAntecedentsMedicaux());
        }
        if (dossierMedicalRequestDto.getAllergies() != null) {
            existingDossierMedical.setAllergies(dossierMedicalRequestDto.getAllergies());
        }
        if (dossierMedicalRequestDto.getTraitementsEnCours() != null) {
            existingDossierMedical.setTraitementsEnCours(dossierMedicalRequestDto.getTraitementsEnCours());
        }
        if (dossierMedicalRequestDto.getObservations() != null) {
            existingDossierMedical.setObservations(dossierMedicalRequestDto.getObservations());
        }

        // --- Handling Collections (Consultations and Prescriptions) ---
        if (dossierMedicalRequestDto.getConsultationSummaries() != null) {
            List<Consultation> updatedConsultations = dossierMedicalRequestDto.getConsultationSummaries().stream()
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

        if (dossierMedicalRequestDto.getPrescriptions() != null) {
            List<Prescription> updatedPrescriptions = dossierMedicalRequestDto.getPrescriptions().stream()
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

        DossierMedicalRequestDto updatedDossier = DossierMedicalRequestDto.fromEntity(
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
    public DossierMedicalRequestDto findDossierMedicalById(Integer id) {
        DossierMedical foundDossierMedical = dossierMedicalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Le Dossier médical avec l'ID " + id + " n'existe pas."));

        historiqueActionService.enregistrerAction(
                "Recherche du dossier médical ID: " + id
        );

        return DossierMedicalRequestDto.fromEntity(foundDossierMedical);
    }



    @Override
    @Transactional()
    public List<DossierMedicalRequestDto> findAllDossierMedical() {
        List<DossierMedicalRequestDto> allDossiers = dossierMedicalRepository.findAll()
                .stream()
                .map(DossierMedicalRequestDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction(
                "Affichage de tous les dossiers médicaux (nombre de résultats: " + allDossiers.size() + ")"
        );

        return allDossiers;
    }



    @Override
    @Transactional()
    public PatientRequestDto findPatientByDossierMedicalId(Integer dossierMedicalId) {
        DossierMedical dossierMedical = dossierMedicalRepository.findById(dossierMedicalId)
                .orElseThrow(() -> new EntityNotFoundException("Dossier médical introuvable avec l'ID " + dossierMedicalId));

        PatientRequestDto patientRequestDto = Optional.ofNullable(dossierMedical.getPatient())
                .map(PatientRequestDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Aucun patient associé au dossier médical avec l'ID " + dossierMedicalId));

        historiqueActionService.enregistrerAction(
                "Recherche du patient associé au dossier médical ID: " + dossierMedicalId +
                        " (Nom du patient: " + patientRequestDto.getInfoPersonnel().getNom() + " " + patientRequestDto.getInfoPersonnel().getPrenom() + ")"
        );

        return patientRequestDto;
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
    public DossierMedicalRequestDto findDossierMedicalByPatientId(Integer patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient introuvable avec l'ID: " + patientId));

        DossierMedical dossierMedical = Optional.ofNullable(patient.getDossierMedical())
                .orElseThrow(() -> new EntityNotFoundException("Dossier médical introuvable pour le patient ID: " + patientId + ". Ce patient n'a pas encore de dossier médical."));

        historiqueActionService.enregistrerAction(
                "Recherche du dossier médical pour le patient ID: " + patientId +
                        " (Date Création: " + dossierMedical.getCreationDate() + ")" // Using getCreationDate
        );
        return DossierMedicalRequestDto.fromEntity(dossierMedical);
    }
}