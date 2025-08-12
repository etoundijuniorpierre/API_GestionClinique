package com.example.GestionClinique.service.serviceImpl;


import com.example.GestionClinique.model.entity.*;
import com.example.GestionClinique.repository.*;
import com.example.GestionClinique.service.HistoriqueActionService;
import com.example.GestionClinique.service.PrescriptionService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final ConsultationRepository consultationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PatientRepository patientRepository;
    private final DossierMedicalRepository dossierMedicalRepository;
    private final HistoriqueActionService historiqueActionService;
    private final LoggingAspect loggingAspect;

        @Override
        public Prescription addPrescription(Long consultationId, Prescription prescription) {

            Consultation consultation = consultationRepository.findById(consultationId)
                    .orElseThrow(() -> new IllegalArgumentException("Consultation not found with ID: " + consultationId));

            prescription.setConsultation(consultation);

            if (consultation.getMedecin() == null ) {
                throw new IllegalStateException("Consultation, its Medecin, cannot add prescription.");
            }
            prescription.setMedecin(consultation.getMedecin());
            prescription.setDossierMedical(consultation.getDossierMedical());
            prescription.setPatient(consultation.getDossierMedical().getPatient());

            Prescription savedPrescription = prescriptionRepository.save(prescription);

            historiqueActionService.enregistrerAction(
                    String.format("Ajout prescription ID: %d pour consultation ID: %d",
                            savedPrescription.getId(), consultationId),
                    loggingAspect.currentUserId()
            );

            return savedPrescription;
    }

    @Override
    public Prescription updatePrescription(Long id, Prescription prescriptionDetails) {
        Prescription existingPrescription = prescriptionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found with ID: " + id));

        // Update scalar fields
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

        historiqueActionService.enregistrerAction(
                String.format("Mise à jour prescription ID: %d", id),
                loggingAspect.currentUserId()
        );

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

        historiqueActionService.enregistrerAction(
                String.format("Suppression prescription ID: %d", id),
                loggingAspect.currentUserId()
        );

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