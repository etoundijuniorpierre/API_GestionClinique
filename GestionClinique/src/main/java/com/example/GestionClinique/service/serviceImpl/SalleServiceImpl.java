package com.example.GestionClinique.service.serviceImpl;

import com.example.GestionClinique.dto.SalleDto;
import com.example.GestionClinique.model.entity.Salle;
import com.example.GestionClinique.model.entity.enumElem.StatutSalle;
import com.example.GestionClinique.repository.RendezVousRepository;
import com.example.GestionClinique.repository.SalleRepository;
import com.example.GestionClinique.service.HistoriqueActionService; // Import HistoriqueActionService
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
    private final HistoriqueActionService historiqueActionService; // Inject HistoriqueActionService

    @Autowired
    public SalleServiceImpl(SalleRepository salleRepository, RendezVousRepository rendezVousRepository, HistoriqueActionService historiqueActionService) { // Add to constructor
        this.salleRepository = salleRepository;
        this.rendezVousRepository = rendezVousRepository;
        this.historiqueActionService = historiqueActionService; // Initialize
    }



    @Override
    @Transactional
    public SalleDto createSalle(SalleDto salleDto) {
        // --- 1. Validation des données du DTO ---
        // Ajoutez des validations pour les champs obligatoires d'une salle
        if (salleDto.getNumero() == null || salleDto.getNumero().trim().isEmpty()) {
            throw new IllegalArgumentException("Le numéro de la salle est obligatoire.");
        }
        // Assurez-vous qu'une salle avec le même numéro n'existe pas déjà (si les numéros doivent être uniques)
        if (salleRepository.findByNumero(salleDto.getNumero()).isPresent()) {
            throw new IllegalArgumentException("Une salle avec le numéro '" + salleDto.getNumero() + "' existe déjà.");
        }
        // Si le statut de la salle est censé avoir une valeur par défaut à la création
        if (salleDto.getStatutSalle() == null) {
            salleDto.setStatutSalle(DISPONIBLE); // Par exemple, les nouvelles salles sont disponibles
        }

        SalleDto savedSalle = SalleDto.fromEntity(salleRepository
                .save(SalleDto.toEntity(salleDto)));

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Création de la salle ID: " + savedSalle.getId() + ", Numéro: " + savedSalle.getNumero() + ", Statut: " + savedSalle.getStatutSalle()
        );
        // --- Fin de l'ajout de l'historique ---

        return savedSalle;
    }



    @Override
    @Transactional
    public SalleDto findSalleById(Integer salleId) {

        SalleDto foundSalle = salleRepository.findById(salleId)
                .map(SalleDto::fromEntity)
                .orElseThrow(() -> new RuntimeException("salle n'existe pas"));

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Recherche de la salle ID: " + salleId + ", Numéro: " + foundSalle.getNumero()
        );
        // --- Fin de l'ajout de l'historique ---

        return foundSalle;
    }



    @Override
    @Transactional
    public List<SalleDto> findAllSalle() {
        List<SalleDto> allSalles = salleRepository.findAll()
                .stream()
                .map(SalleDto::fromEntity)
                .collect(Collectors.toList());

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Affichage de toutes les salles."
        );
        // --- Fin de l'ajout de l'historique ---

        return allSalles;
    }



    @Override
    @Transactional
    public SalleDto updateSalle(Integer salleId, SalleDto salleDto) {

        Salle existingSalle = salleRepository.findById(salleId)
                .orElseThrow(() -> new IllegalArgumentException("Salle avec l'id " + salleId + " n'existe pas"));

        // Stocker l'état avant la mise à jour pour le logging
        String oldNumero = existingSalle.getNumero();
        String oldServiceMedical = String.valueOf(existingSalle.getServiceMedical()); // Assuming String
        StatutSalle oldStatutSalle = existingSalle.getStatutSalle();

        // --- Mise à jour sélective des champs ---
        if (salleDto.getNumero() != null && !salleDto.getNumero().trim().isEmpty()) {
            // Si le numéro est changé, vérifier l'unicité
            if (!salleDto.getNumero().equals(existingSalle.getNumero())) {
                if (salleRepository.findByNumero(salleDto.getNumero()).isPresent()) {
                    throw new IllegalArgumentException("Une salle avec le numéro '" + salleDto.getNumero() + "' existe déjà.");
                }
            }
            existingSalle.setNumero(salleDto.getNumero());
        }
        if (salleDto.getServiceMedical() != null) {
            existingSalle.setServiceMedical(salleDto.getServiceMedical());
        }
        if (salleDto.getStatutSalle() != null) {
            existingSalle.setStatutSalle(salleDto.getStatutSalle());
        }

        SalleDto updatedSalle = SalleDto.fromEntity(
                salleRepository.save(
                        existingSalle
                )
        );

        // --- Ajout de l'historique ---
        StringBuilder logMessage = new StringBuilder("Mise à jour de la salle ID: " + salleId + ".");
        if (!Objects.equals(oldNumero, updatedSalle.getNumero())) logMessage.append(" Numéro: ").append(oldNumero).append(" -> ").append(updatedSalle.getNumero());
        if (!Objects.equals(oldServiceMedical, updatedSalle.getServiceMedical())) logMessage.append(" Service Médical: ").append(oldServiceMedical).append(" -> ").append(updatedSalle.getServiceMedical());
        if (!Objects.equals(oldStatutSalle, updatedSalle.getStatutSalle())) logMessage.append(" Statut: ").append(oldStatutSalle).append(" -> ").append(updatedSalle.getStatutSalle());

        historiqueActionService.enregistrerAction(logMessage.toString());
        // --- Fin de l'ajout de l'historique ---

        return updatedSalle;
    }



    @Override
    @Transactional
    public void deleteSalle(Integer salleId) {
        Salle salleToDelete = salleRepository.findById(salleId)
                .orElseThrow(() -> new EntityNotFoundException("La salle avec l'ID " + salleId + " n'existe pas et ne peut pas être supprimée."));

        if (rendezVousRepository.existsBySalle(salleToDelete)) {
            historiqueActionService.enregistrerAction(
                    "Tentative de suppression de la salle ID: " + salleId + ": échec, car associée à des rendez-vous."
            );
            throw new IllegalStateException("Impossible de supprimer la salle avec l'ID " + salleId + " car elle est associée à un ou plusieurs rendez-vous.");
        }

        salleRepository.deleteById(salleId);

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Suppression de la salle ID: " + salleId + ", Numéro: " + salleToDelete.getNumero()
        );
        // --- Fin de l'ajout de l'historique ---
    }



    @Override
    @Transactional
    public List<SalleDto> findSallesByStatut(StatutSalle statutSalle) {

        if (statutSalle == null) {
            throw new IllegalArgumentException("Le statut ne peut pas être nul.");
        }

        List<SalleDto> sallesByStatut = salleRepository.findSallesByStatut(statutSalle)
                .stream()
                .filter(Objects::nonNull)
                .map(SalleDto::fromEntity)
                .collect(Collectors.toList());

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Recherche des salles avec le statut: " + statutSalle + " (nombre de résultats: " + sallesByStatut.size() + ")"
        );
        // --- Fin de l'ajout de l'historique ---

        return sallesByStatut;
    }



    @Override
    @Transactional
    public List<SalleDto> findAvailableSalles(LocalDateTime dateHeureDebut, Integer dureeMinutes) { // Ajouté 'dureeMinutes' pour une meilleure gestion des créneaux
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
        LocalTime heureFin = dateHeureDebut.plusMinutes(dureeMinutes).toLocalTime(); // Calculate end time

        // --- 3. Récupérer toutes les salles potentiellement disponibles ---
        List<Salle> toutesLesSallesDisponiblesG_ = (List<Salle>) salleRepository.findSallesByStatut(DISPONIBLE); // Assuming you only want 'DISPONIBLE'

        // --- 4. Filtrer les salles qui sont déjà occupées par un RendezVous à ce créneau ---
        List<SalleDto> availableSalles = toutesLesSallesDisponiblesG_.stream()
                .filter(salle -> {
                    // Check if there's any overlapping appointment for this room
                    // This often requires a more sophisticated query in your RendezVousRepository
                    // For example: `findBySalleAndJourAndHeureBetween(salle, jour, heureDebut, heureFin)`
                    // Or, for simple cases, check if any existing appointment at that exact start time
                    boolean estOccupeeParRendezVous = rendezVousRepository
                            .findBySalleAndJourAndHeure(salle, jour, heureDebut) // This is a simplistic check; for proper overlap, you need a more complex query.
                            .isPresent();
                    return !estOccupeeParRendezVous; // The room is available if it's not occupied
                })
                .map(SalleDto::fromEntity)
                .collect(Collectors.toList());

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Recherche de salles disponibles pour le " + dateHeureDebut.toLocalDate() + " à " + dateHeureDebut.toLocalTime() +
                        " pour " + dureeMinutes + " minutes (nombre de salles trouvées: " + availableSalles.size() + ")"
        );
        // --- Fin de l'ajout de l'historique ---

        return availableSalles;
    }
}