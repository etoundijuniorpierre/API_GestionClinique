package com.example.GestionClinique.controller;

import com.example.GestionClinique.controller.controllerApi.ConsultationApi;
import com.example.GestionClinique.dto.RequestDto.ConsultationRequestDto;
import com.example.GestionClinique.dto.RequestDto.DossierMedicalRequestDto;
import com.example.GestionClinique.dto.RequestDto.PrescriptionRequestDto;
import com.example.GestionClinique.dto.RequestDto.RendezVousRequestDto;
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
    public ConsultationRequestDto createConsultation(ConsultationRequestDto consultationRequestDto) {
        return consultationService.createConsultation(consultationRequestDto);
    }

    @Override
    public ConsultationRequestDto startConsultation(Integer id, ConsultationRequestDto consultationRequestDto) {
        return consultationService.startConsultation(id, consultationRequestDto);
    }

    @Override
    public ConsultationRequestDto updateConsultation(Integer id, ConsultationRequestDto consultationRequestDto) {
        return consultationService.updateConsultation(id, consultationRequestDto);
    }

    @Override
    public ConsultationRequestDto findById(Integer id) {
        return consultationService.findById(id);
    }

    @Override
    public List<ConsultationRequestDto> findAll() {
        return consultationService.findAll();
    }

    @Override
    public DossierMedicalRequestDto findDossierMedicalByConsultationId(Integer id) {
        return consultationService.findDossierMedicalByConsultationId(id);
    }

    @Override
    public RendezVousRequestDto findRendezVousByConsultationId(Integer id) {
        return consultationService.findRendezVousByConsultationId(id);
    }

    @Override
    public void deleteById(Integer id) {
        consultationService.deleteById(id);
    }

    @Override
    public ConsultationRequestDto addPrescriptionToConsultation(Integer id, PrescriptionRequestDto prescriptionRequestDto) {
        return consultationService.addPrescriptionToConsultation(id, prescriptionRequestDto);
    }

    @Override
    public List<PrescriptionRequestDto> findPrescriptionsByConsultationId(Integer id) {
        return consultationService.findPrescriptionsByConsultationId(id);
    }
}
