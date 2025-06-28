package com.example.GestionClinique.controller;


import com.example.GestionClinique.controller.controllerApi.RendezVousApi;
import com.example.GestionClinique.dto.RequestDto.RendezVousRequestDto;
import com.example.GestionClinique.model.entity.Salle;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import com.example.GestionClinique.service.RendezVousService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
public class RendezVousController implements RendezVousApi {

    private final RendezVousService rendezVousService;

    @Autowired
    public RendezVousController(RendezVousService rendezVousService) {
        this.rendezVousService = rendezVousService;
    }

    @Override
    public RendezVousRequestDto createRendezVous(RendezVousRequestDto rendezVousRequestDto) {
        return rendezVousService.createRendezVous(rendezVousRequestDto);
    }

    @Override
    public RendezVousRequestDto findRendezVousById(Integer id) {
        return rendezVousService.findRendezVousById(id);
    }

    @Override
    public RendezVousRequestDto updateRendezVous(Integer id, RendezVousRequestDto rendezVousRequestDto) {
        return rendezVousService.updateRendezVous(id, rendezVousRequestDto);
    }

    @Override
    public void deleteRendezVous(Integer id) {
        rendezVousService.deleteRendezVous(id);
    }

    @Override
    public List<RendezVousRequestDto> findAllRendezVous() {
        return rendezVousService.findAllRendezVous();
    }

    @Override
    public List<RendezVousRequestDto> findRendezVousByStatut(StatutRDV statut) {
        return rendezVousService.findRendezVousByStatut(statut);
    }

    @Override
    public List<RendezVousRequestDto> findRendezVousBySalleId(Integer id) {
        return rendezVousService.findRendezVousBySalleId(id);
    }

    @Override
    public List<RendezVousRequestDto> findRendezVousByPatientId(Integer id) {
        return rendezVousService.findRendezVousByPatientId(id);
    }

    @Override
    public List<RendezVousRequestDto> findRendezVousByMedecinId(Integer id) {
        return rendezVousService.findRendezVousByMedecinId(id);
    }

    @Override
    public List<RendezVousRequestDto> findRendezVousByJour(LocalDate jour) {
        return rendezVousService.findRendezVousByJour(jour);
    }

    @Override
    public boolean isRendezVousAvailable(LocalDate jour, LocalTime heure, Utilisateur medecin, Salle salle) {
        return rendezVousService.isRendezVousAvailable(jour, heure, medecin, salle);
    }

    @Override
    public RendezVousRequestDto cancelRendezVous(Integer idRendezVous) {
        return rendezVousService.cancelRendezVous(idRendezVous);
    }
}
