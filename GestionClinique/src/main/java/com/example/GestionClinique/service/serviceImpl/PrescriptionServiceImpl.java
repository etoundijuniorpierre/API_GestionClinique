package com.example.GestionClinique.service.serviceImpl;

import com.example.GestionClinique.dto.RequestDto.PrescriptionRequestDto;
import com.example.GestionClinique.model.entity.*;
import com.example.GestionClinique.repository.*;
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
@Transactional
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final ConsultationRepository consultationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PatientRepository patientRepository;
    private final DossierMedicalRepository dossierMedicalRepository;

    @Autowired
    public PrescriptionServiceImpl(PrescriptionRepository prescriptionRepository,
                                   ConsultationRepository consultationRepository,
                                   UtilisateurRepository utilisateurRepository,
                                   PatientRepository patientRepository,
                                   DossierMedicalRepository dossierMedicalRepository) {
        this.prescriptionRepository = prescriptionRepository;
        this.consultationRepository = consultationRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.patientRepository = patientRepository;
        this.dossierMedicalRepository = dossierMedicalRepository;
    }

    @Override
    public Prescription createPrescription(Prescription prescription) {
        // Fetch and set associated entities based on their IDs from the incoming Prescription object
        Consultation consultation = consultationRepository.findById(prescription.getConsultation().getId())
                .orElseThrow(() -> new IllegalArgumentException("Consultation not found with ID: " + prescription.getConsultation().getId()));
        Utilisateur medecin = utilisateurRepository.findById(prescription.getMedecin().getId())
                .orElseThrow(() -> new IllegalArgumentException("Medecin not found with ID: " + prescription.getMedecin().getId()));
        Patient patient = patientRepository.findById(prescription.getPatient().getId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + prescription.getPatient().getId()));
        DossierMedical dossierMedical = dossierMedicalRepository.findById(prescription.getDossierMedical().getId())
                .orElseThrow(() -> new IllegalArgumentException("DossierMedical not found with ID: " + prescription.getDossierMedical().getId()));

        prescription.setConsultation(consultation);
        prescription.setMedecin(medecin);
        prescription.setPatient(patient);
        prescription.setDossierMedical(dossierMedical);

        return prescriptionRepository.save(prescription);
    }

    @Override
    public Prescription updatePrescription(Long id, Prescription prescriptionDetails) {
        Prescription existingPrescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found with ID: " + id));

        // Update scalar fields
        existingPrescription.setDatePrescription(prescriptionDetails.getDatePrescription());
        existingPrescription.setTypePrescription(prescriptionDetails.getTypePrescription());
        existingPrescription.setMedicaments(prescriptionDetails.getMedicaments());
        existingPrescription.setInstructions(prescriptionDetails.getInstructions());
        existingPrescription.setDureePrescription(prescriptionDetails.getDureePrescription());
        existingPrescription.setQuantite(prescriptionDetails.getQuantite());

        // Update associated entities if their IDs are provided and differ
        if (prescriptionDetails.getConsultation() != null && !prescriptionDetails.getConsultation().getId().equals(existingPrescription.getConsultation().getId())) {
            Consultation newConsultation = consultationRepository.findById(prescriptionDetails.getConsultation().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Consultation not found with ID: " + prescriptionDetails.getConsultation().getId()));
            existingPrescription.setConsultation(newConsultation);
        }
        if (prescriptionDetails.getMedecin() != null && !prescriptionDetails.getMedecin().getId().equals(existingPrescription.getMedecin().getId())) {
            Utilisateur newMedecin = utilisateurRepository.findById(prescriptionDetails.getMedecin().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Medecin not found with ID: " + prescriptionDetails.getMedecin().getId()));
            existingPrescription.setMedecin(newMedecin);
        }
        if (prescriptionDetails.getPatient() != null && !prescriptionDetails.getPatient().getId().equals(existingPrescription.getPatient().getId())) {
            Patient newPatient = patientRepository.findById(prescriptionDetails.getPatient().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + prescriptionDetails.getPatient().getId()));
            existingPrescription.setPatient(newPatient);
        }
        if (prescriptionDetails.getDossierMedical() != null && !prescriptionDetails.getDossierMedical().getId().equals(existingPrescription.getDossierMedical().getId())) {
            DossierMedical newDossierMedical = dossierMedicalRepository.findById(prescriptionDetails.getDossierMedical().getId())
                    .orElseThrow(() -> new IllegalArgumentException("DossierMedical not found with ID: " + prescriptionDetails.getDossierMedical().getId()));
            existingPrescription.setDossierMedical(newDossierMedical);
        }

        return prescriptionRepository.save(existingPrescription);
    }

    @Override
    @Transactional
    public Prescription findById(Long id) {
        return prescriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found with ID: " + id));
    }

    @Override
    @Transactional
    public List<Prescription> findAllPrescription() {
        return prescriptionRepository.findAll();
    }

    @Override
    public void deletePrescription(Long id) {
        Prescription prescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found with ID: " + id));
        // Add business logic checks if necessary, e.g., cannot delete if consultation is finalized.
        prescriptionRepository.delete(prescription);
    }

    @Override
    @Transactional
    public List<Prescription> findPrescriptionByMedecinId(Long medecinId) {
        return prescriptionRepository.findByMedecinId(medecinId);
    }

    @Override
    @Transactional
    public List<Prescription> findPrescriptionByPatientId(Long patientId) {
        return prescriptionRepository.findByPatientId(patientId);
    }

    @Override
    @Transactional
    public List<Prescription> findPrescriptionByConsultationId(Long consultationId) {
        return prescriptionRepository.findByConsultationId(consultationId);
    }
}