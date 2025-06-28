package com.example.GestionClinique.service.serviceImpl;

import com.example.GestionClinique.model.entity.Patient;
import com.example.GestionClinique.repository.PatientRepository;
import com.example.GestionClinique.service.PatientService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    @Autowired
    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Transactional
    @Override
    public Patient createPatient(Patient patient) {
        // You might add checks here, e.g., if a patient with the same email already exists
        if (patient.getEmail() != null && patientRepository.findByEmail(patient.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Patient with email " + patient.getEmail() + " already exists.");
        }
        return patientRepository.save(patient);
    }

    @Transactional
    @Override
    public Patient updatePatient(Long id, Patient patientDetails) {
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + id));

        if (patientDetails.getEmail() != null && !patientDetails.getEmail().equals(existingPatient.getEmail())) {
            if (patientRepository.findByEmail(patientDetails.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email " + patientDetails.getEmail() + " is already taken by another patient.");
            }
        }

        existingPatient.setNom(patientDetails.getNom());
        existingPatient.setPrenom(patientDetails.getPrenom());
        existingPatient.setEmail(patientDetails.getEmail());
        existingPatient.setAdresse(patientDetails.getAdresse());
        existingPatient.setTelephone(patientDetails.getTelephone());
        existingPatient.setDateNaissance(patientDetails.getDateNaissance());
        existingPatient.setGenre(patientDetails.getGenre());


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
        patientRepository.delete(patient);
    }

    @Transactional
    @Override
    public List<Patient> searchPatients(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().length() < 3) {
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
}