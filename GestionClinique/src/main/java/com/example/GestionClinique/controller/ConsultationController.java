package com.example.GestionClinique.controller;

import com.example.GestionClinique.controller.controllerApi.ConsultationApi;
import com.example.GestionClinique.dto.ConsultationDto;
import com.example.GestionClinique.dto.DossierMedicalDto;
import com.example.GestionClinique.dto.PrescriptionDto;
import com.example.GestionClinique.dto.RendezVousDto;
import com.example.GestionClinique.service.ConsultationService;
import com.example.GestionClinique.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ConsultationController implements ConsultationApi {
    private final ConsultationService consultationService;
    private final PrescriptionService prescriptionService;

    @Autowired
    public ConsultationController(ConsultationService consultationService, PrescriptionService prescriptionService) {
        this.consultationService = consultationService;
        this.prescriptionService = prescriptionService;
    }

    @Override
    public ConsultationDto createConsultation(ConsultationDto consultationDto) {
        return consultationService.createConsultation(consultationDto);
    }

    @Override
    public ConsultationDto startConsultation(Integer id, ConsultationDto consultationDto) {
        return consultationService.startConsultation(id, consultationDto);
    }

    @Override
    public ConsultationDto updateConsultation(Integer id, ConsultationDto consultationDto) {
        return consultationService.updateConsultation(id, consultationDto);
    }

    @Override
    public ConsultationDto findById(Integer id) {
        return consultationService.findById(id);
    }

    @Override
    public List<ConsultationDto> findAll() {
        return consultationService.findAll();
    }

    @Override
    public DossierMedicalDto findDossierMedicalByConsultationId(Integer id) {
        return consultationService.findDossierMedicalByConsultationId(id);
    }

    @Override
    public RendezVousDto findRendezVousByConsultationId(Integer id) {
        return consultationService.findRendezVousByConsultationId(id);
    }

    @Override
    public void deleteById(Integer id) {
        consultationService.deleteById(id);
    }

    @Override
    public ConsultationDto addPrescriptionToConsultation(Integer id, PrescriptionDto prescriptionDto) {
        return consultationService.addPrescriptionToConsultation(id, prescriptionDto);
    }

    @Override
    public List<PrescriptionDto> findPrescriptionsByConsultationId(Integer id) {
        return consultationService.findPrescriptionsByConsultationId(id);
    }
}
