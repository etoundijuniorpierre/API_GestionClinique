package com.example.GestionClinique.service;

import com.example.GestionClinique.dto.SalleDto;
import com.example.GestionClinique.model.entity.enumElem.StatutSalle;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface SalleService {
    SalleDto createSalle(SalleDto salleDto);
    SalleDto findSalleById(Integer id);
    List<SalleDto> findAllSalle();
    SalleDto updateSalle(Integer id, SalleDto salleDto);
    void deleteSalle(Integer id);

    // Nouvelle méthode: Trouver les salles par statut (enum StatutSalle)
    List<SalleDto> findSallesByStatut(StatutSalle statutSalle);

    List<SalleDto> findAvailableSalles(LocalDateTime dateHeureDebut, Integer dureeMinutes);
}
