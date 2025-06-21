package com.example.GestionClinique.controller;


import com.example.GestionClinique.controller.controllerApi.PrescriptionApi;
import com.example.GestionClinique.dto.PrescriptionDto;
import com.example.GestionClinique.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PrescriptionController implements PrescriptionApi {
    private final PrescriptionService prescriptionService;

    @Autowired
    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @Override
    public PrescriptionDto createPrescription(PrescriptionDto prescriptionDto) {
        return prescriptionService.createPrescription(prescriptionDto);
    }

    @Override
    public PrescriptionDto updatePrescription(Integer id, PrescriptionDto prescriptionDto) {
        return prescriptionService.updatePrescription(id, prescriptionDto);
    }

    @Override
    public PrescriptionDto findById(Integer id) {
        return prescriptionService.findById(id);
    }

    @Override
    public List<PrescriptionDto> findAllPrescription() {
        return prescriptionService.findAllPrescription();
    }

    @Override
    public void deletePrescription(Integer id) {
        prescriptionService.deletePrescription(id);
    }

    @Override
    public List<PrescriptionDto> findPrescriptionByMedecinId(Integer id) {
        return prescriptionService.findPrescriptionByMedecinId(id);
    }

    @Override
    public List<PrescriptionDto> findPrescriptionByPatientId(Integer id) {
        return prescriptionService.findPrescriptionByPatientId(id);
    }

    @Override
    public List<PrescriptionDto> findPrescriptionByConsultationId(Integer id) {
        return prescriptionService.findPrescriptionByConsultationId(id);
    }
}


