package com.example.GestionClinique.controller;


import com.example.GestionClinique.controller.controllerApi.PrescriptionApi;
import com.example.GestionClinique.dto.RequestDto.PrescriptionRequestDto;
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
    public PrescriptionRequestDto createPrescription(PrescriptionRequestDto prescriptionRequestDto) {
        return prescriptionService.createPrescription(prescriptionRequestDto);
    }

    @Override
    public PrescriptionRequestDto updatePrescription(Integer id, PrescriptionRequestDto prescriptionRequestDto) {
        return prescriptionService.updatePrescription(id, prescriptionRequestDto);
    }

    @Override
    public PrescriptionRequestDto findById(Integer id) {
        return prescriptionService.findById(id);
    }

    @Override
    public List<PrescriptionRequestDto> findAllPrescription() {
        return prescriptionService.findAllPrescription();
    }

    @Override
    public void deletePrescription(Integer id) {
        prescriptionService.deletePrescription(id);
    }

    @Override
    public List<PrescriptionRequestDto> findPrescriptionByMedecinId(Integer id) {
        return prescriptionService.findPrescriptionByMedecinId(id);
    }

    @Override
    public List<PrescriptionRequestDto> findPrescriptionByPatientId(Integer id) {
        return prescriptionService.findPrescriptionByPatientId(id);
    }

    @Override
    public List<PrescriptionRequestDto> findPrescriptionByConsultationId(Integer id) {
        return prescriptionService.findPrescriptionByConsultationId(id);
    }
}


