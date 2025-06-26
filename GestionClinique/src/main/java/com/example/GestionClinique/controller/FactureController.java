package com.example.GestionClinique.controller;


import com.example.GestionClinique.controller.controllerApi.FactureApi;
import com.example.GestionClinique.dto.FactureDto;
import com.example.GestionClinique.dto.PatientDto;
import com.example.GestionClinique.dto.RendezVousDto;
import com.example.GestionClinique.model.entity.enumElem.ModePaiement;
import com.example.GestionClinique.model.entity.enumElem.StatutPaiement;
import com.example.GestionClinique.service.FactureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    public FactureDto createFactureForConsultation(Integer consultationId, FactureDto factureDto) {
        return factureService.createFactureForConsultation(consultationId, factureDto);
    }

    @Override
    public FactureDto updateFacture(Integer id, FactureDto factureDto) {
        return factureService.updateFacture(id, factureDto);
    }

    @Override
    public List<FactureDto> findAllFactures() {
        return factureService.findAllFactures();
    }

    @Override
    public List<FactureDto> findFacturesByStatut(StatutPaiement statutPaiement) {
        return factureService.findFacturesByStatut(statutPaiement);
    }

    @Override
    public List<FactureDto> findFacturesByModePaiement(ModePaiement modePaiement) {
        return factureService.findFacturesByModePaiement(modePaiement);
    }

    @Override
    public FactureDto findById(Integer id) {
        return factureService.findById(id);
    }

    @Override
    public void deleteFacture(Integer id) {
        factureService.deleteFacture(id);
    }

    @Override
    public PatientDto findPatientByFactureId(Integer id) {
        return factureService.findPatientByFactureId(id);
    }

    @Override
    public FactureDto updateStatutPaiement(Integer id, StatutPaiement nouveauStatut) {
        return factureService.updateStatutPaiement(id, nouveauStatut);
    }
}
