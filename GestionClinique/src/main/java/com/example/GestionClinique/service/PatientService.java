package com.example.GestionClinique.service;


import com.example.GestionClinique.model.entity.Patient;
import com.example.GestionClinique.model.entity.RendezVous;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import jakarta.transaction.Transactional;

import java.util.List;


public interface PatientService {

    Patient createPatient(Patient patient);

    Patient updatePatient(Long id, Patient patientDetails);

    List<Patient> findAllPatients();

    Patient findById(Long id);

    void deletePatient(Long id);

    List<Patient> searchPatients(String searchTerm);

    List<Patient> findPatientByNom(String nom);

    Patient findPatientByEmail(String email);

    List<RendezVous> findRendezVousByPatientSearchTerm(String patientName);// Renamed parameter for clarity

    List<RendezVous> findRendezVousForPatientByStatus(String patientName, StatutRDV statut);
}
