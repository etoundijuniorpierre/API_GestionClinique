package com.example.GestionClinique.controller;

import com.example.GestionClinique.controller.controllerApi.SalleApi;
import com.example.GestionClinique.dto.RequestDto.SalleResquestDto;
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
    public SalleResquestDto createSalle(SalleResquestDto SalleResquestDto) {
        return salleService.createSalle(SalleResquestDto);
    }

    @Override
    public SalleResquestDto findSalleById(Integer id) {
        return salleService.findSalleById(id);
    }

    @Override
    public List<SalleResquestDto> findSalleByStatut(StatutSalle statutSalle) {
        return salleService.findSallesByStatut(statutSalle);
    }

    @Override
    public List<SalleResquestDto> findAllSalle() {
        return salleService.findAllSalle();
    }

    @Override
    public SalleResquestDto updateSalle(Integer id, SalleResquestDto SalleResquestDto) {
        return salleService.updateSalle(id, SalleResquestDto);
    }

    @Override
    public void deleteSalle(Integer id) {
        salleService.deleteSalle(id);
    }

    @Override
    public List<SalleResquestDto> findAvailableSalles(LocalDateTime dateHeureDebut, Integer dureeMinutes) {
        return salleService.findAvailableSalles(dateHeureDebut, dureeMinutes);
    }



}
