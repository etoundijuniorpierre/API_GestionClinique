package com.example.GestionClinique.service;


import com.example.GestionClinique.dto.PatientDto;

import java.util.List;


public interface PatientService {
    PatientDto createPatient(PatientDto patientDto);
    PatientDto updatePatient(Integer id, PatientDto patientDto);
    List<PatientDto> findAllPatients();
    PatientDto findById(Integer id);
    void deletePatient(Integer id);

    // Nouvelle méthode: Rechercher des patients (par nom, prénom, email, etc.)
    List<PatientDto> searchPatients(String searchTerm);
    // Nouvelle méthode: Trouver un patient par son nom (si nom unique ou pour recherche partielle)
    List<PatientDto> findPatientByInfoPersonnel_Nom(String nom);
    // Nouvelle méthode: Trouver un patient par son email (si email unique)
    PatientDto findPatientByInfoPersonnel_Email(String email);
}
