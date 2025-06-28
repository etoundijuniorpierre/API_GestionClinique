package com.example.GestionClinique.service;


import com.example.GestionClinique.model.entity.Patient;
import jakarta.transaction.Transactional;

import java.util.List;


public interface PatientService {


    Patient createPatient(Patient patient);

    Patient updatePatient(Long id, Patient patientDetails);

    @Transactional
    List<Patient> findAllPatients();

    @Transactional
    Patient findById(Long id);

    void deletePatient(Long id);

    @Transactional
    List<Patient> searchPatients(String searchTerm);

    @Transactional
    List<Patient> findPatientByNom(String nom);

    @Transactional
    Patient findPatientByEmail(String email);
}
