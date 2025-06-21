package com.example.GestionClinique.controller;

import com.example.GestionClinique.controller.controllerApi.DossierMedicalApi;
import com.example.GestionClinique.dto.ConsultationDto;
import com.example.GestionClinique.dto.DossierMedicalDto;
import com.example.GestionClinique.dto.PatientDto;
import com.example.GestionClinique.service.DossierMedicalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class DossierMedicalController implements DossierMedicalApi {

    private final DossierMedicalService dossierMedicalService;

    @Autowired
    public DossierMedicalController(DossierMedicalService service) {
        this.dossierMedicalService = service;
    }

    @Override
    public DossierMedicalDto createDossierMedicalForPatient(DossierMedicalDto dossierMedicalDto, Integer idPatient) {
        return dossierMedicalService.createDossierMedicalForPatient(idPatient, dossierMedicalDto);
    }

    @Override
    public DossierMedicalDto updateDossierMedical(Integer id, DossierMedicalDto dossierMedicalDto) {
        return dossierMedicalService.updateDossierMedical(id, dossierMedicalDto);
    }

    @Override
    public DossierMedicalDto findDossierMedicalById(Integer id) {
        return dossierMedicalService.findDossierMedicalById(id);
    }

    @Override
    public List<DossierMedicalDto> findAllDossierMedical() {
        return dossierMedicalService.findAllDossierMedical();
    }

    @Override
    public PatientDto findPatientByDossierMedicalId(Integer id) {
        return dossierMedicalService.findPatientByDossierMedicalId(id);
    }

    @Override
    public void deleteDossierMedical(Integer id) {
            dossierMedicalService.deleteDossierMedicalById(id);
    }


    @Override
    public DossierMedicalDto findDossierMedicalByPatientId(Integer id) {
        return dossierMedicalService.findDossierMedicalByPatientId(id);
    }
}
