package com.example.GestionClinique.service;

import com.example.GestionClinique.dto.RequestDto.DossierMedicalRequestDto;
import com.example.GestionClinique.dto.RequestDto.PatientRequestDto;

import java.util.List;


public interface DossierMedicalService {
//    DossierMedicalDto createDossierMedical(DossierMedicalDto dossierMedicalDto); // Ajouté: Création initiale
    DossierMedicalRequestDto createDossierMedicalForPatient(Integer patientId, DossierMedicalRequestDto dossierMedicalRequestDto); // Ajouté: Création liée à un patient
    DossierMedicalRequestDto updateDossierMedical(Integer id, DossierMedicalRequestDto dossierMedicalRequestDto);
    DossierMedicalRequestDto findDossierMedicalById(Integer id);
    List<DossierMedicalRequestDto> findAllDossierMedical();
    PatientRequestDto findPatientByDossierMedicalId(Integer id); // Renommé pour clarté
    void deleteDossierMedicalById(Integer id);
    // Nouvelle méthode: Trouver un dossier médical par l'ID du patient
    DossierMedicalRequestDto findDossierMedicalByPatientId(Integer patientId);
}
