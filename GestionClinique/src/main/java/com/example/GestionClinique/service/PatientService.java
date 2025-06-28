package com.example.GestionClinique.service;


import com.example.GestionClinique.dto.RequestDto.PatientRequestDto;

import java.util.List;


public interface PatientService {
    PatientRequestDto createPatient(PatientRequestDto patientRequestDto);
    PatientRequestDto updatePatient(Integer id, PatientRequestDto patientRequestDto);
    List<PatientRequestDto> findAllPatients();
    PatientRequestDto findById(Integer id);
    void deletePatient(Integer id);

    // Nouvelle méthode: Rechercher des patients (par nom, prénom, email, etc.)
    List<PatientRequestDto> searchPatients(String searchTerm);
    // Nouvelle méthode: Trouver un patient par son nom (si nom unique ou pour recherche partielle)
    List<PatientRequestDto> findPatientByInfoPersonnel_Nom(String nom);
    // Nouvelle méthode: Trouver un patient par son email (si email unique)
    PatientRequestDto findPatientByInfoPersonnel_Email(String email);
}
