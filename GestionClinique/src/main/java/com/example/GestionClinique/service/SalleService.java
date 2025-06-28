package com.example.GestionClinique.service;

import com.example.GestionClinique.dto.RequestDto.SalleResquestDto;
import com.example.GestionClinique.model.entity.enumElem.StatutSalle;

import java.time.LocalDateTime;
import java.util.List;

public interface SalleService {
    SalleResquestDto createSalle(SalleResquestDto salleResquestDto);
    SalleResquestDto findSalleById(Integer id);
    List<SalleResquestDto> findAllSalle();
    SalleResquestDto updateSalle(Integer id, SalleResquestDto salleResquestDto);
    void deleteSalle(Integer id);

    // Nouvelle méthode: Trouver les salles par statut (enum StatutSalle)
    List<SalleResquestDto> findSallesByStatut(StatutSalle statutSalle);

    List<SalleResquestDto> findAvailableSalles(LocalDateTime dateHeureDebut, Integer dureeMinutes);
}
