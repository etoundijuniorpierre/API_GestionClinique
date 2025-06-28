package com.example.GestionClinique.controller;


import com.example.GestionClinique.controller.controllerApi.PatientApi;
import com.example.GestionClinique.dto.RequestDto.PatientRequestDto;
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
    public PatientRequestDto createPatient(PatientRequestDto patientRequestDto) {
        return patientService.createPatient(patientRequestDto);
    }

    @Override
    public PatientRequestDto updatePatient(Integer id, PatientRequestDto patientRequestDto) {
        return patientService.updatePatient(id, patientRequestDto);
    }

    @Override
    public List<PatientRequestDto> findAllPatients() {
        return patientService.findAllPatients();
    }

    @Override
    public PatientRequestDto findById(Integer id) {
        return patientService.findById(id);
    }

    @Override
    public void deletePatient(Integer id) {
        patientService.deletePatient(id);
    }

    @Override
    public List<PatientRequestDto> searchPatients(String searchTerm) {
        return patientService.searchPatients(searchTerm);
    }

    @Override
    public List<PatientRequestDto> findPatientByInfoPersonnel_Nom(String nom) {
        return patientService.findPatientByInfoPersonnel_Nom(nom);
    }

    @Override
    public PatientRequestDto findPatientByInfoPersonnel_Email(String email) {
        return patientService.findPatientByInfoPersonnel_Email(email);
    }
}
