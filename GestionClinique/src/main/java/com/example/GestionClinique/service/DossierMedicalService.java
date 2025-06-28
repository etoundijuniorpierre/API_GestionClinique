package com.example.GestionClinique.service;


import com.example.GestionClinique.dto.RequestDto.PatientRequestDto;
import com.example.GestionClinique.model.entity.DossierMedical;
import com.example.GestionClinique.model.entity.Patient;

import java.util.List;


public interface DossierMedicalService {
    DossierMedical createDossierMedicalForPatient(Long patientId, DossierMedical dossierMedical);
    DossierMedical updateDossierMedical(Long id, DossierMedical dossierMedicalDetails);
    DossierMedical findDossierMedicalById(Long id);
    List<DossierMedical> findAllDossierMedical();
    Patient findPatientByDossierMedicalId(Long id); // Returns Patient entity
    void deleteDossierMedicalById(Long id);
    DossierMedical findDossierMedicalByPatientId(Long patientId);
}
