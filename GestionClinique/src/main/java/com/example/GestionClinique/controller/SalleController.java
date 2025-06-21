package com.example.GestionClinique.controller;

import com.example.GestionClinique.controller.controllerApi.SalleApi;
import com.example.GestionClinique.dto.SalleDto;
import com.example.GestionClinique.model.entity.enumElem.StatutSalle;
import com.example.GestionClinique.service.SalleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public class SalleController implements SalleApi {
    private final SalleService salleService;

     @Autowired
    public SalleController(SalleService salleService) {
        this.salleService = salleService;
    }

    @Override
    public SalleDto createSalle(SalleDto SalleDto) {
        return salleService.createSalle(SalleDto);
    }

    @Override
    public SalleDto findSalleById(Integer id) {
        return salleService.findSalleById(id);
    }

    @Override
    public List<SalleDto> findSalleByStatut(StatutSalle statutSalle) {
        return salleService.findSallesByStatut(statutSalle);
    }

    @Override
    public List<SalleDto> findAllSalle() {
        return salleService.findAllSalle();
    }

    @Override
    public SalleDto updateSalle(Integer id, SalleDto SalleDto) {
        return salleService.updateSalle(id, SalleDto);
    }

    @Override
    public void deleteSalle(Integer id) {
        salleService.deleteSalle(id);
    }

    @Override
    public List<SalleDto> findAvailableSalles(LocalDateTime dateHeureDebut, Integer dureeMinutes) {
        return salleService.findAvailableSalles(dateHeureDebut, dureeMinutes);
    }



}
