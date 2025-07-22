package com.example.GestionClinique.service;


import com.example.GestionClinique.model.entity.Salle;
import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import com.example.GestionClinique.model.entity.enumElem.StatutSalle;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface SalleService {
    Salle createSalle(Salle salle);

    Salle findSalleById(Long id);

    List<Salle> findAllSalle();

    Salle updateSalle(Long id, Salle salleDetails);

    void deleteSalle(Long id);

    List<Salle> findSallesByStatut(StatutSalle statutSalle);
    
//    List<Salle> findAvailableSalles(LocalDateTime dateHeureDebut, Long dureeMinutes);

    Salle findSallesByServiceMedical(ServiceMedical serviceMedical);
}
