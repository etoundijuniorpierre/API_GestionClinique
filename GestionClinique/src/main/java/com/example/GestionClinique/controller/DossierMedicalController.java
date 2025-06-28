package com.example.GestionClinique.controller;

import com.example.GestionClinique.controller.controllerApi.DossierMedicalApi;
import com.example.GestionClinique.dto.RequestDto.DossierMedicalRequestDto;
import com.example.GestionClinique.dto.RequestDto.PatientRequestDto;
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
    public DossierMedicalRequestDto createDossierMedicalForPatient(DossierMedicalRequestDto dossierMedicalRequestDto, Integer idPatient) {
        return dossierMedicalService.createDossierMedicalForPatient(idPatient, dossierMedicalRequestDto);
    }

    @Override
    public DossierMedicalRequestDto updateDossierMedical(Integer id, DossierMedicalRequestDto dossierMedicalRequestDto) {
        return dossierMedicalService.updateDossierMedical(id, dossierMedicalRequestDto);
    }

    @Override
    public DossierMedicalRequestDto findDossierMedicalById(Integer id) {
        return dossierMedicalService.findDossierMedicalById(id);
    }

    @Override
    public List<DossierMedicalRequestDto> findAllDossierMedical() {
        return dossierMedicalService.findAllDossierMedical();
    }

    @Override
    public PatientRequestDto findPatientByDossierMedicalId(Integer id) {
        return dossierMedicalService.findPatientByDossierMedicalId(id);
    }

    @Override
    public void deleteDossierMedical(Integer id) {
            dossierMedicalService.deleteDossierMedicalById(id);
    }


    @Override
    public DossierMedicalRequestDto findDossierMedicalByPatientId(Integer id) {
        return dossierMedicalService.findDossierMedicalByPatientId(id);
    }
}
