package com.example.GestionClinique.service.serviceImpl;

import com.example.GestionClinique.model.entity.DossierMedical;
import com.example.GestionClinique.model.entity.Patient;
import com.example.GestionClinique.model.entity.RendezVous;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import com.example.GestionClinique.repository.PatientRepository;
import com.example.GestionClinique.repository.RendezVousRepository;
import com.example.GestionClinique.service.HistoriqueActionService;
import com.example.GestionClinique.service.PatientService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@AllArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final RendezVousRepository rendezVousRepository;
    private final HistoriqueActionService historiqueActionService;
    private final LoggingAspect loggingAspect;


    @Transactional
    @Override
    public Patient createPatient(Patient patient) {
        DossierMedical dossierMedical = patient.getDossierMedical();
        if (dossierMedical != null) {
            dossierMedical.setPatient(patient);
        } else {
            throw new IllegalArgumentException("Dossier médical manquant pour la création du patient.");
        }
        patient.setAge((long) Period.between(patient.getDateNaissance(), LocalDate.now()).getYears());
        Patient savedPatient = patientRepository.save(patient);

        historiqueActionService.enregistrerAction(
                String.format("Création patient ID: %d - %s %s",
                        savedPatient.getId(), savedPatient.getNom(), savedPatient.getPrenom()),
                loggingAspect.currentUserId()
        );

        return savedPatient;
    }

    @Transactional
    @Override
    public Patient updatePatient(Long id, Patient patientDetails) {
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient non trouvé avec ID: " + id));

        if (patientDetails.getEmail() != null && !patientDetails.getEmail().equals(existingPatient.getEmail())) {
            if (patientRepository.findByEmail(patientDetails.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email " + patientDetails.getEmail() + " est déjà utilisé par un autre patient.");
            }
            existingPatient.setEmail(patientDetails.getEmail());
        }

        if (patientDetails.getNom() != null) {
            existingPatient.setNom(patientDetails.getNom());
        }
        if (patientDetails.getPrenom() != null) {
            existingPatient.setPrenom(patientDetails.getPrenom());
        }
        // Ajoutez des conditions pour tous les champs restants
        if (patientDetails.getAdresse() != null) {
            existingPatient.setAdresse(patientDetails.getAdresse());
        }
        if (patientDetails.getTelephone() != null) {
            existingPatient.setTelephone(patientDetails.getTelephone());
        }
        if (patientDetails.getDateNaissance() != null) {
            existingPatient.setDateNaissance(patientDetails.getDateNaissance());
        }
        if (patientDetails.getGenre() != null) {
            existingPatient.setGenre(patientDetails.getGenre());
        }

        historiqueActionService.enregistrerAction(
                String.format("Mise à jour patient ID: %d", id),
                loggingAspect.currentUserId()
        );

        return patientRepository.save(existingPatient);
    }


    @Transactional
    @Override
    public List<Patient> findAllPatients() {
        return patientRepository.findAll();
    }

    @Transactional
    @Override
    public Patient findById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + id));
    }

    @Transactional
    @Override
    public void deletePatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + id));

        historiqueActionService.enregistrerAction(
                String.format("Suppression patient ID: %d - %s %s",
                        patient.getId(), patient.getNom(), patient.getPrenom()),
                loggingAspect.currentUserId()
        );

        patientRepository.delete(patient);
    }

    @Transactional
    @Override
    public List<Patient> searchPatients(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().length() < 2) {
            return List.of();
        }
        return patientRepository.searchByTerm(searchTerm);
    }

    @Transactional
    @Override
    public List<Patient> findPatientByNom(String nom) {
        return patientRepository.findByNom(nom);
    }

    @Transactional
    @Override
    public Patient findPatientByEmail(String email) {
        return patientRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with email: " + email));
    }

    @Override
    public List<RendezVous> findRendezVousByPatientSearchTerm(String patientSearchTerm) {
        return rendezVousRepository.findRendezVousByPatientSearchTerm(patientSearchTerm);
    }

    @Override
    public List<RendezVous> findRendezVousForPatientByStatus(String patientName, StatutRDV statut) {
        return rendezVousRepository.findRendezVousForPatientByStatus(patientName, statut);
    }
}