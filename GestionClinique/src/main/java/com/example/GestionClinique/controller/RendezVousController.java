package com.example.GestionClinique.controller;


import com.example.GestionClinique.controller.controllerApi.RendezVousApi;
import com.example.GestionClinique.dto.RendezVousDto;
import com.example.GestionClinique.model.entity.Salle;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import com.example.GestionClinique.service.RendezVousService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public RendezVousDto createRendezVous(RendezVousDto rendezVousDto) {
        return rendezVousService.createRendezVous(rendezVousDto);
    }

    @Override
    public RendezVousDto findRendezVousById(Integer id) {
        return rendezVousService.findRendezVousById(id);
    }

    @Override
    public RendezVousDto updateRendezVous(Integer id, RendezVousDto rendezVousDto) {
        return rendezVousService.updateRendezVous(id, rendezVousDto);
    }

    @Override
    public void deleteRendezVous(Integer id) {
        rendezVousService.deleteRendezVous(id);
    }

    @Override
    public List<RendezVousDto> findAllRendezVous() {
        return rendezVousService.findAllRendezVous();
    }

    @Override
    public List<RendezVousDto> findRendezVousByStatut(StatutRDV statut) {
        return rendezVousService.findRendezVousByStatut(statut);
    }

    @Override
    public List<RendezVousDto> findRendezVousBySalleId(Integer id) {
        return rendezVousService.findRendezVousBySalleId(id);
    }

    @Override
    public List<RendezVousDto> findRendezVousByPatientId(Integer id) {
        return rendezVousService.findRendezVousByPatientId(id);
    }

    @Override
    public List<RendezVousDto> findRendezVousByMedecinId(Integer id) {
        return rendezVousService.findRendezVousByMedecinId(id);
    }

    @Override
    public List<RendezVousDto> findRendezVousByJour(LocalDate jour) {
        return rendezVousService.findRendezVousByJour(jour);
    }

    @Override
    public boolean isRendezVousAvailable(LocalDate jour, LocalTime heure, Utilisateur medecin, Salle salle) {
        return rendezVousService.isRendezVousAvailable(jour, heure, medecin, salle);
    }

    @Override
    public RendezVousDto cancelRendezVous(Integer idRendezVous) {
        return rendezVousService.cancelRendezVous(idRendezVous);
    }
}
