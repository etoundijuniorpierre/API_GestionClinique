package com.example.GestionClinique.service;


import com.example.GestionClinique.dto.RequestDto.RendezVousRequestDto;
import com.example.GestionClinique.model.entity.RendezVous;
import com.example.GestionClinique.model.entity.Salle;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


public interface RendezVousService {
    RendezVous createRendezVous(RendezVous rendezVous);

    RendezVous findRendezVousById(Long id);

    RendezVous updateRendezVous(Long id, RendezVous rendezVousDetails);

    void deleteRendezVous(Long id);

    List<RendezVous> findAllRendezVous();

    List<RendezVous> findRendezVousByStatut(StatutRDV statut);

    List<RendezVous> findRendezVousBySalleId(Long salleId);

    List<RendezVous> findRendezVousByPatientId(Long patientId);

    List<RendezVous> findRendezVousByMedecinId(Long medecinId); // Renamed parameter for clarity

    boolean isRendezVousAvailable(LocalDate jour, LocalTime heure, Long medecinId, Long salleId); // Parameters changed to IDs

    @Transactional(readOnly = true)
    boolean isRendezVousAvailableForUpdate(Long rendezVousId, LocalDate jour, LocalTime heure, Long medecinId, Long salleId);

    RendezVous cancelRendezVous(Long rendezVousId);

    List<RendezVous> findRendezVousByJour(LocalDate jour);
}