package com.example.GestionClinique.controller;


import com.example.GestionClinique.controller.controllerApi.FactureApi;
import com.example.GestionClinique.dto.RequestDto.FactureRequestDto;
import com.example.GestionClinique.dto.RequestDto.PatientRequestDto;
import com.example.GestionClinique.model.entity.enumElem.ModePaiement;
import com.example.GestionClinique.model.entity.enumElem.StatutPaiement;
import com.example.GestionClinique.service.FactureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FactureController implements FactureApi {

    private final FactureService factureService;

    @Autowired
    public FactureController(FactureService factureService) {
        this.factureService = factureService;
    }

    @Override
    public FactureRequestDto createFactureForConsultation(Integer consultationId, FactureRequestDto factureRequestDto) {
        return factureService.createFactureForConsultation(consultationId, factureRequestDto);
    }

    @Override
    public FactureRequestDto updateFacture(Integer id, FactureRequestDto factureRequestDto) {
        return factureService.updateFacture(id, factureRequestDto);
    }

    @Override
    public List<FactureRequestDto> findAllFactures() {
        return factureService.findAllFactures();
    }

    @Override
    public List<FactureRequestDto> findFacturesByStatut(StatutPaiement statutPaiement) {
        return factureService.findFacturesByStatut(statutPaiement);
    }

    @Override
    public List<FactureRequestDto> findFacturesByModePaiement(ModePaiement modePaiement) {
        return factureService.findFacturesByModePaiement(modePaiement);
    }

    @Override
    public FactureRequestDto findById(Integer id) {
        return factureService.findById(id);
    }

    @Override
    public void deleteFacture(Integer id) {
        factureService.deleteFacture(id);
    }

    @Override
    public PatientRequestDto findPatientByFactureId(Integer id) {
        return factureService.findPatientByFactureId(id);
    }

    @Override
    public FactureRequestDto updateStatutPaiement(Integer id, StatutPaiement nouveauStatut) {
        return factureService.updateStatutPaiement(id, nouveauStatut);
    }
}
