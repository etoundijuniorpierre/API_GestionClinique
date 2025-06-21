package com.example.GestionClinique.controller;


import com.example.GestionClinique.controller.controllerApi.PatientApi;
import com.example.GestionClinique.dto.PatientDto;
import com.example.GestionClinique.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PatientController implements PatientApi {
    private final PatientService patientService;

    @Autowired
    public PatientController(PatientService patientService) {

        this.patientService = patientService;
    }

    @Override
    public PatientDto createPatient(PatientDto patientDto) {
        return patientService.createPatient(patientDto);
    }

    @Override
    public PatientDto updatePatient(Integer id, PatientDto patientDto) {
        return patientService.updatePatient(id, patientDto);
    }

    @Override
    public List<PatientDto> findAllPatients() {
        return patientService.findAllPatients();
    }

    @Override
    public PatientDto findById(Integer id) {
        return patientService.findById(id);
    }

    @Override
    public void deletePatient(Integer id) {
        patientService.deletePatient(id);
    }

    @Override
    public List<PatientDto> searchPatients(String searchTerm) {
        return patientService.searchPatients(searchTerm);
    }

    @Override
    public List<PatientDto> findPatientByInfoPersonnel_Nom(String nom) {
        return patientService.findPatientByInfoPersonnel_Nom(nom);
    }

    @Override
    public PatientDto findPatientByInfoPersonnel_Email(String email) {
        return patientService.findPatientByInfoPersonnel_Email(email);
    }
}
