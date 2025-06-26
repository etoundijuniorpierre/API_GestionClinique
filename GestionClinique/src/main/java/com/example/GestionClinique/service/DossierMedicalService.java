package com.example.GestionClinique.service;

import com.example.GestionClinique.dto.DossierMedicalDto;
import com.example.GestionClinique.dto.PatientDto;
import jakarta.transaction.Transactional;

import java.util.List;


public interface DossierMedicalService {
//    DossierMedicalDto createDossierMedical(DossierMedicalDto dossierMedicalDto); // Ajouté: Création initiale
    DossierMedicalDto createDossierMedicalForPatient(Integer patientId, DossierMedicalDto dossierMedicalDto); // Ajouté: Création liée à un patient
    DossierMedicalDto updateDossierMedical(Integer id, DossierMedicalDto dossierMedicalDto);
    DossierMedicalDto findDossierMedicalById(Integer id);
    List<DossierMedicalDto> findAllDossierMedical();
    PatientDto findPatientByDossierMedicalId(Integer id); // Renommé pour clarté
    void deleteDossierMedicalById(Integer id);
    // Nouvelle méthode: Trouver un dossier médical par l'ID du patient
    DossierMedicalDto findDossierMedicalByPatientId(Integer patientId);
}
