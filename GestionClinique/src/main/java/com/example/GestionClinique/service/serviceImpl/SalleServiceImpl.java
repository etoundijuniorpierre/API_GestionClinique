package com.example.GestionClinique.service.serviceImpl;

import com.example.GestionClinique.dto.RequestDto.SalleResquestDto;
import com.example.GestionClinique.model.entity.Salle;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV; // Needed for availability checks
import com.example.GestionClinique.model.entity.enumElem.StatutSalle;
import com.example.GestionClinique.repository.RendezVousRepository;
import com.example.GestionClinique.repository.SalleRepository;
import com.example.GestionClinique.service.HistoriqueActionService;
import com.example.GestionClinique.service.SalleService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.example.GestionClinique.model.entity.enumElem.StatutSalle.DISPONIBLE;


@Service
public class SalleServiceImpl implements SalleService {
    private final SalleRepository salleRepository;
    private final RendezVousRepository rendezVousRepository;
    private final HistoriqueActionService historiqueActionService;

    @Autowired
    public SalleServiceImpl(SalleRepository salleRepository, RendezVousRepository rendezVousRepository, HistoriqueActionService historiqueActionService) {
        this.salleRepository = salleRepository;
        this.rendezVousRepository = rendezVousRepository;
        this.historiqueActionService = historiqueActionService;
    }



    @Override
    @Transactional
    public SalleResquestDto createSalle(SalleResquestDto salleResquestDto) {
        // --- Input Validation ---
        if (salleResquestDto.getNumero() == null || salleResquestDto.getNumero().trim().isEmpty()) {
            throw new IllegalArgumentException("Le numéro de la salle est obligatoire et ne peut pas être vide.");
        }
        if (salleRepository.findByNumero(salleResquestDto.getNumero()).isPresent()) {
            throw new IllegalArgumentException("Une salle avec le numéro '" + salleResquestDto.getNumero() + "' existe déjà.");
        }

        // --- DTO to Entity Conversion ---
        Salle salleToSave = new Salle(); // Manually create entity
        salleToSave.setNumero(salleResquestDto.getNumero());
        salleToSave.setServiceMedical(salleResquestDto.getServiceMedical()); // Assuming ServiceMedical is an Enum or String
        // Set default status if not provided
        salleToSave.setStatutSalle(salleResquestDto.getStatutSalle() != null ? salleResquestDto.getStatutSalle() : DISPONIBLE);

        // --- Save Entity ---
        Salle savedSalleEntity = salleRepository.save(salleToSave);
        SalleResquestDto savedSalleResquestDto = SalleResquestDto.fromEntity(savedSalleEntity);

        // --- Historique Logging ---
        historiqueActionService.enregistrerAction(
                "Création de la salle ID: " + savedSalleResquestDto.getId() + ", Numéro: " + savedSalleResquestDto.getNumero() + ", Statut: " + savedSalleResquestDto.getStatutSalle()
        );
        return savedSalleResquestDto;
    }



    @Override
    @Transactional() // Mark as read-only
    public SalleResquestDto findSalleById(Integer salleId) {
        Salle foundSalle = salleRepository.findById(salleId)
                .orElseThrow(() -> new EntityNotFoundException("La salle avec l'ID " + salleId + " n'existe pas.")); // Use EntityNotFoundException

        SalleResquestDto foundSalleResquestDto = SalleResquestDto.fromEntity(foundSalle);

        historiqueActionService.enregistrerAction(
                "Recherche de la salle ID: " + salleId + ", Numéro: " + foundSalleResquestDto.getNumero()
        );
        return foundSalleResquestDto;
    }



    @Override
    @Transactional() // Mark as read-only
    public List<SalleResquestDto> findAllSalle() {
        List<SalleResquestDto> allSalles = salleRepository.findAll().stream()
                .map(SalleResquestDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction(
                "Affichage de toutes les salles (nombre de résultats: " + allSalles.size() + ")."
        );
        return allSalles;
    }



    @Override
    @Transactional
    public SalleResquestDto updateSalle(Integer salleId, SalleResquestDto salleResquestDto) {
        Salle existingSalle = salleRepository.findById(salleId)
                .orElseThrow(() -> new EntityNotFoundException("La salle avec l'ID " + salleId + " n'existe pas.")); // Use EntityNotFoundException

        // Store old values for logging
        String oldNumero = existingSalle.getNumero();
        String oldServiceMedical = (existingSalle.getServiceMedical() != null) ? existingSalle.getServiceMedical().name() : "N/A"; // Handle null ServiceMedical
        StatutSalle oldStatutSalle = existingSalle.getStatutSalle();


        // --- Selective Field Update ---
        if (salleResquestDto.getNumero() != null && !salleResquestDto.getNumero().trim().isEmpty()) {
            if (!salleResquestDto.getNumero().equals(existingSalle.getNumero())) {
                // Check if the new number is already taken by another salle
                if (salleRepository.findByNumero(salleResquestDto.getNumero()).isPresent()) {
                    throw new IllegalArgumentException("Une salle avec le numéro '" + salleResquestDto.getNumero() + "' existe déjà.");
                }
            }
            existingSalle.setNumero(salleResquestDto.getNumero());
        }
        if (salleResquestDto.getServiceMedical() != null) {
            existingSalle.setServiceMedical(salleResquestDto.getServiceMedical());
        }
        if (salleResquestDto.getStatutSalle() != null) {
            existingSalle.setStatutSalle(salleResquestDto.getStatutSalle());
        }



        SalleResquestDto updatedSalle = SalleResquestDto.fromEntity(salleRepository.save(existingSalle));

        // --- Historique Logging ---
        StringBuilder logMessage = new StringBuilder("Mise à jour de la salle ID: " + salleId + ".");
        if (!Objects.equals(oldNumero, updatedSalle.getNumero())) logMessage.append(" Numéro: ").append(oldNumero).append(" -> ").append(updatedSalle.getNumero()).append(".");
        if (!Objects.equals(oldServiceMedical, updatedSalle.getServiceMedical() != null ? updatedSalle.getServiceMedical().name() : "N/A")) logMessage.append(" Service Médical: ").append(oldServiceMedical).append(" -> ").append(updatedSalle.getServiceMedical() != null ? updatedSalle.getServiceMedical().name() : "N/A").append(".");
        if (!Objects.equals(oldStatutSalle, updatedSalle.getStatutSalle())) logMessage.append(" Statut: ").append(oldStatutSalle).append(" -> ").append(updatedSalle.getStatutSalle()).append(".");



        historiqueActionService.enregistrerAction(logMessage.toString());
        return updatedSalle;
    }



    @Override
    @Transactional
    public void deleteSalle(Integer salleId) {
        Salle salleToDelete = salleRepository.findById(salleId)
                .orElseThrow(() -> new EntityNotFoundException("La salle avec l'ID " + salleId + " n'existe pas et ne peut pas être supprimée."));

        // Check if there are any associated rendez-vous (even if not PLANIFIE/CONFIRME, they still exist)
        if (rendezVousRepository.existsBySalle(salleToDelete)) {
            historiqueActionService.enregistrerAction(
                    "Tentative de suppression de la salle ID: " + salleId + ": échec, car associée à des rendez-vous existants."
            );
            throw new IllegalStateException("Impossible de supprimer la salle avec l'ID " + salleId + " car elle est associée à un ou plusieurs rendez-vous. Veuillez d'abord supprimer ou réaffecter ces rendez-vous.");
        }

        salleRepository.deleteById(salleId);

        historiqueActionService.enregistrerAction(
                "Suppression de la salle ID: " + salleId + ", Numéro: " + salleToDelete.getNumero()
        );
    }



    @Override
    @Transactional()
    public List<SalleResquestDto> findSallesByStatut(StatutSalle statutSalle) {
        if (statutSalle == null) {
            throw new IllegalArgumentException("Le statut ne peut pas être nul pour la recherche.");
        }

        List<SalleResquestDto> sallesByStatut = salleRepository.findByStatutSalle(statutSalle).stream() // Assuming findByStatutSalle method
                .filter(Objects::nonNull)
                .map(SalleResquestDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction(
                "Recherche des salles avec le statut: " + statutSalle + " (nombre de résultats: " + sallesByStatut.size() + ")"
        );
        return sallesByStatut;
    }



    @Override
    @Transactional()
    public List<SalleResquestDto> findAvailableSalles(LocalDateTime dateHeureDebut, Integer dureeMinutes) {
        // --- 1. Validation des entrées ---
        if (dateHeureDebut == null) {
            throw new IllegalArgumentException("La date et l'heure de début pour la recherche des salles disponibles sont obligatoires.");
        }
        if (dureeMinutes == null || dureeMinutes <= 0) {
            throw new IllegalArgumentException("La durée du rendez-vous en minutes est obligatoire et doit être positive.");
        }

        // --- 2. Définir la plage horaire de recherche ---
        LocalDate jour = dateHeureDebut.toLocalDate();
        LocalTime heureDebut = dateHeureDebut.toLocalTime();
        LocalTime heureFin = dateHeureDebut.plusMinutes(dureeMinutes).toLocalTime();

        // --- 3. Récupérer toutes les salles potentiellement disponibles ---
        // Fetch all rooms that are marked as DISPONIBLE
        List<Salle> allAvailableStatusSalles = salleRepository.findByStatutSalle(DISPONIBLE);

        // --- 4. Filtrer les salles qui n'ont pas de rendez-vous qui chevauchent ---
        // Iterate through rooms and check for conflicts at the given time slot
        List<SalleResquestDto> trulyAvailableSalles = allAvailableStatusSalles.stream()
                .filter(salle -> {
                    boolean isOccupied = rendezVousRepository.findBySalleAndJourAndHeureAndStatutIn(salle, jour, heureDebut, List.of(StatutRDV.PLANIFIE, StatutRDV.CONFIRME)).isPresent();

                    boolean isAlreadyBooked = rendezVousRepository.findBySalleAndJourAndHeureBetween(salle, jour, heureDebut, heureFin).isPresent();

                    boolean hasConflict = rendezVousRepository.findBySalleAndJourAndHeureAndStatutIn(salle, jour, heureDebut, List.of(StatutRDV.PLANIFIE, StatutRDV.CONFIRME)).isPresent();
                    return !hasConflict;
                })
                .map(SalleResquestDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction(
                "Recherche de salles disponibles pour le " + dateHeureDebut.toLocalDate() + " à " + dateHeureDebut.toLocalTime() +
                        " pour " + dureeMinutes + " minutes (nombre de salles trouvées: " + trulyAvailableSalles.size() + ")"
        );
        return trulyAvailableSalles;
    }
}