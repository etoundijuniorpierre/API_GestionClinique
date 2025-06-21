package com.example.GestionClinique.service.serviceImpl;

import com.example.GestionClinique.dto.PatientDto;
import com.example.GestionClinique.dto.PrescriptionDto;
import com.example.GestionClinique.dto.UtilisateurDto;
import com.example.GestionClinique.model.entity.Consultation;
import com.example.GestionClinique.model.entity.Patient;
import com.example.GestionClinique.model.entity.Prescription;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.repository.ConsultationRepository;
import com.example.GestionClinique.repository.PatientRepository;
import com.example.GestionClinique.repository.PrescriptionRepository;
import com.example.GestionClinique.repository.UtilisateurRepository;
import com.example.GestionClinique.service.HistoriqueActionService; // Import HistoriqueActionService
import com.example.GestionClinique.service.PrescriptionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final ConsultationRepository consultationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PatientRepository patientRepository;
    private final HistoriqueActionService historiqueActionService; // Inject HistoriqueActionService

    @Autowired
    public PrescriptionServiceImpl(PrescriptionRepository prescriptionRepository,
                                   ConsultationRepository consultationRepository,
                                   UtilisateurRepository utilisateurRepository,
                                   PatientRepository patientRepository,
                                   HistoriqueActionService historiqueActionService) { // Add to constructor
        this.prescriptionRepository = prescriptionRepository;
        this.consultationRepository = consultationRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.patientRepository = patientRepository;
        this.historiqueActionService = historiqueActionService; // Initialize
    }



    @Override
    @Transactional
    public PrescriptionDto createPrescription(PrescriptionDto prescriptionDto) {
        if(prescriptionDto.getConsultation() == null || prescriptionDto.getConsultation().getId() == null ) {
            throw new IllegalArgumentException("Une prescription doit être associée à une consultation existante.");
        }

        // on s'assure le l'existance de consultation
        Consultation consultation = consultationRepository.findById(prescriptionDto.getConsultation().getId())
                .orElseThrow(() -> new RuntimeException("La consultation avec l'ID " + prescriptionDto.getConsultation().getId() + " n'existe pas."));

        // on mappe le ddto de precription et on le lie à consultation
        Prescription newPrescription = PrescriptionDto.toEntity(prescriptionDto);
        newPrescription.setConsultation(consultation);

        // lier la prescritpion à un médecin (utilisateur) et un patient
        if (consultation.getMedecin() != null) {
            newPrescription.setMedecin(consultation.getMedecin());
        } else {
            throw new IllegalStateException("Le médecin n'est pas défini pour la consultation.");
        }
        // Lier la prescription au patient via le dossier médical
        if (consultation.getDossierMedical() != null && consultation.getDossierMedical().getPatient() != null) {
            newPrescription.setPatient(consultation.getDossierMedical().getPatient());
        } else {
            throw new IllegalStateException("Le patient n'est pas défini pour la consultation.");
        }

        // Définir la date de prescription par défaut
        if (newPrescription.getDatePrescription() == null) {
            newPrescription.setDatePrescription(LocalDate.now());
        }

        PrescriptionDto savedPrescription = PrescriptionDto.fromEntity(
                prescriptionRepository.save(
                        newPrescription
                )
        );

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Création de la prescription ID: " + savedPrescription.getId() + " pour la consultation ID: " + consultation.getId() +
                        " (Médecin: " + (consultation.getMedecin() != null ? consultation.getMedecin().getInfoPersonnel().getNom() + " " + consultation.getMedecin().getInfoPersonnel().getPrenom() : "N/A") +
                        ", Patient: " + (consultation.getDossierMedical() != null && consultation.getDossierMedical().getPatient() != null ? consultation.getDossierMedical().getPatient().getInfoPersonnel().getNom() + " " + consultation.getDossierMedical().getPatient().getInfoPersonnel().getPrenom() : "N/A") + ")"
        );
        // --- Fin de l'ajout de l'historique ---

        return savedPrescription;
    }



    @Override
    @Transactional
    public PrescriptionDto updatePrescription(Integer prescriptionId, PrescriptionDto prescriptionDto) {

        Prescription existingPrescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new RuntimeException("prescription avec l'Id "+prescriptionId+" n'existe pas"));

        //datePrescription n'est pas pertinente
        // existingPrescription.setDatePrescription(prescriptionDto.getDatePrescription());
        existingPrescription.setTypePrescription(prescriptionDto.getTypePrescription());
        existingPrescription.setMedicaments(prescriptionDto.getMedicaments());
        existingPrescription.setInstructions(prescriptionDto.getInstructions());
        existingPrescription.setDureePrescription(prescriptionDto.getDureePrescription());
        existingPrescription.setQuantite(prescriptionDto.getQuantite());

        PrescriptionDto updatedPrescription = PrescriptionDto.fromEntity(
                prescriptionRepository.save(
                        existingPrescription
                )
        );

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Mise à jour de la prescription ID: " + prescriptionId +
                        " (Consultation ID: " + (existingPrescription.getConsultation() != null ? existingPrescription.getConsultation().getId() : "N/A") + ")"
        );
        // --- Fin de l'ajout de l'historique ---

        return updatedPrescription;
    }



    @Override
    @Transactional
    public PrescriptionDto findById(Integer prescriptionId) {

        PrescriptionDto foundPrescription = prescriptionRepository.findById(prescriptionId)
                .map(PrescriptionDto::fromEntity)
                .orElseThrow(() -> new RuntimeException("Le prescription id " + prescriptionId + " n'existe pas"));

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Recherche de la prescription ID: " + prescriptionId
        );
        // --- Fin de l'ajout de l'historique ---

        return foundPrescription;
    }



    @Override
    @Transactional
    public List<PrescriptionDto> findAllPrescription() {

        List<PrescriptionDto> allPrescriptions = prescriptionRepository.findAll()
                .stream()
                .map(PrescriptionDto::fromEntity)
                .collect(Collectors.toList());

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Affichage de toutes les prescriptions."
        );
        // --- Fin de l'ajout de l'historique ---

        return allPrescriptions;
    }



    @Override
    @Transactional
    public void deletePrescription(Integer prescriptionId) {
        if (!prescriptionRepository.existsById(prescriptionId)) {
            throw new RuntimeException("la prescription n'existe pas");
        }
        prescriptionRepository.deleteById(prescriptionId);

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Suppression de la prescription ID: " + prescriptionId
        );
        // --- Fin de l'ajout de l'historique ---
    }



    @Override
    @Transactional
    public List<PrescriptionDto> findPrescriptionByMedecinId(Integer utilisateurId) {

        Utilisateur existingUtilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("L'utilisateur avec l'ID " + utilisateurId + " n'existe pas."));

        if (existingUtilisateur.getPrescriptions() == null) { // Changed from getHistoriqueActions() to getPrescriptions()
            return List.of();
        }

        List<PrescriptionDto> prescriptions = existingUtilisateur.getPrescriptions()
                .stream()
                .filter(Objects::nonNull)
                .map(PrescriptionDto::fromEntity)
                .collect(Collectors.toList());

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Recherche des prescriptions par médecin ID: " + utilisateurId
        );
        // --- Fin de l'ajout de l'historique ---

        return prescriptions;
    }



    @Override
    @Transactional
    public List<PrescriptionDto> findPrescriptionByPatientId(Integer patientId) { // Corrected parameter name to patientId

        Patient existingPatient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Le patient avec l'ID " + patientId + " n'existe pas."));

        if (existingPatient.getPrescriptions() == null) {
            return List.of();
        }

        List<PrescriptionDto> prescriptions = existingPatient.getPrescriptions()
                .stream()
                .filter(Objects::nonNull)
                .map(PrescriptionDto::fromEntity)
                .collect(Collectors.toList());

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Recherche des prescriptions par patient ID: " + patientId
        );
        // --- Fin de l'ajout de l'historique ---

        return prescriptions;
    }



    @Override
    @Transactional
    public List<PrescriptionDto> findPrescriptionByConsultationId(Integer consultationId) {
        Consultation existingConsultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new EntityNotFoundException("La consultation avec l'ID " + consultationId + " n'existe pas."));

        if (existingConsultation.getPrescriptions() == null) {
            return List.of();
        }

        List<PrescriptionDto> prescriptions = existingConsultation.getPrescriptions()
                .stream()
                .filter(Objects::nonNull)
                .map(PrescriptionDto::fromEntity)
                .collect(Collectors.toList());

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Recherche des prescriptions par consultation ID: " + consultationId
        );
        // --- Fin de l'ajout de l'historique ---

        return prescriptions;
    }
}