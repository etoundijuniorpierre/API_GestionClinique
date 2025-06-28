package com.example.GestionClinique.controller;


import com.example.GestionClinique.controller.controllerApi.HistoriqueActionApi;
import com.example.GestionClinique.dto.RequestDto.HistoriqueActionRequestDto;
import com.example.GestionClinique.service.HistoriqueActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class HistoriqueActionController implements HistoriqueActionApi {
    HistoriqueActionService historiqueActionService;

    @Autowired
    public HistoriqueActionController(HistoriqueActionService historiqueActionService) {
        this.historiqueActionService = historiqueActionService;
    }
//
//    @Override
//    public HistoriqueActionDto save(HistoriqueActionDto historiqueActionDto) {
//        return historiqueActionService.save(historiqueActionDto);
//    }


    @Override
    public List<HistoriqueActionRequestDto> findAll() {
        return historiqueActionService.findAll();
    }

    @Override
    public HistoriqueActionRequestDto findById(Integer id) {
        return historiqueActionService.findById(id);
    }

    @Override
    public List<HistoriqueActionRequestDto> findByUtilisateurId(Integer id) {
        return historiqueActionService.findHistoriqueByUtilisateurId(id);
    }

    @Override
    public List<HistoriqueActionRequestDto> findByDateAfterAndDateBefore(LocalDate startDate, LocalDate endDate) {
        return historiqueActionService.findByDateAfterAndDateBefore(startDate, endDate);
    }
}
