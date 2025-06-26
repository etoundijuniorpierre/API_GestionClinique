package com.example.GestionClinique.service.serviceImpl;

import com.example.GestionClinique.dto.PatientSummaryDto; // Import if not already
import com.example.GestionClinique.dto.RendezVousDto;
import com.example.GestionClinique.dto.SalleSummaryDto;     // Import if not already
import com.example.GestionClinique.dto.UtilisateurSummaryDto; // Import if not already
import com.example.GestionClinique.model.entity.Patient;
import com.example.GestionClinique.model.entity.RendezVous;
import com.example.GestionClinique.model.entity.Salle;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import com.example.GestionClinique.model.entity.enumElem.StatutSalle; // Keep if Salle has a status enum
import com.example.GestionClinique.repository.PatientRepository;
import com.example.GestionClinique.repository.RendezVousRepository;
import com.example.GestionClinique.repository.SalleRepository;
import com.example.GestionClinique.repository.UtilisateurRepository;
import com.example.GestionClinique.service.HistoriqueActionService;
import com.example.GestionClinique.service.RendezVousService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime; // Potentially useful if you track exact timestamp of creation/update
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RendezVousServiceImpl implements RendezVousService {
    private final RendezVousRepository rendezVousRepository;
    private final UtilisateurRepository utilisateurRepository; // For Medecin
    private final SalleRepository salleRepository;
    private final PatientRepository patientRepository;
    private final HistoriqueActionService historiqueActionService;

    @Autowired
    public RendezVousServiceImpl(RendezVousRepository rendezVousRepository,
                                 UtilisateurRepository utilisateurRepository,
                                 SalleRepository salleRepository,
                                 PatientRepository patientRepository,
                                 HistoriqueActionService historiqueActionService) {
        this.rendezVousRepository = rendezVousRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.salleRepository = salleRepository;
        this.patientRepository = patientRepository;
        this.historiqueActionService = historiqueActionService;
    }



    @Override
    @Transactional
    public RendezVousDto createRendezVous(RendezVousDto rendezVousDto) {
        // --- Input Validation ---
        if (rendezVousDto.getJour() == null || rendezVousDto.getHeure() == null) {
            throw new IllegalArgumentException("La date et l'heure du rendez-vous sont obligatoires.");
        }
        // Validate IDs from Summary DTOs
        if (rendezVousDto.getPatientSummary() == null || rendezVousDto.getPatientSummary().getId() == null ||
                rendezVousDto.getMedecinSummary() == null || rendezVousDto.getMedecinSummary().getId() == null ||
                rendezVousDto.getSalleSummary() == null || rendezVousDto.getSalleSummary().getId() == null) {
            throw new IllegalArgumentException("Le patient, le médecin et la salle sont obligatoires pour un rendez-vous (IDs manquants).");
        }

        // Ensure default status if not provided
        if (rendezVousDto.getStatut() == null) {
            rendezVousDto.setStatut(StatutRDV.PLANIFIE);
        }

        // --- Fetch Related Entities ---
        Patient patient = patientRepository.findById(rendezVousDto.getPatientSummary().getId())
                .orElseThrow(() -> new EntityNotFoundException("Patient non trouvé avec l'ID: " + rendezVousDto.getPatientSummary().getId()));
        Utilisateur medecin = utilisateurRepository.findById(rendezVousDto.getMedecinSummary().getId())
                .orElseThrow(() -> new EntityNotFoundException("Médecin non trouvé avec l'ID: " + rendezVousDto.getMedecinSummary().getId()));
        Salle salle = salleRepository.findById(rendezVousDto.getSalleSummary().getId())
                .orElseThrow(() -> new EntityNotFoundException("Salle non trouvée avec l'ID: " + rendezVousDto.getSalleSummary().getId()));

        // --- Check for Availability (using the dedicated helper method) ---
        if (!isRendezVousAvailable(rendezVousDto.getJour(), rendezVousDto.getHeure(), medecin, salle)) {
            throw new IllegalStateException("Le médecin ou la salle n'est pas disponible à cette heure.");
        }

        // --- DTO to Entity Conversion & Relationship Setting ---
        RendezVous rendezVousToSave = new RendezVous(); // Manually create entity
        rendezVousToSave.setJour(rendezVousDto.getJour());
        rendezVousToSave.setHeure(rendezVousDto.getHeure());
        rendezVousToSave.setStatut(rendezVousDto.getStatut());
        rendezVousToSave.setNotes(rendezVousDto.getNotes());
        rendezVousToSave.setServiceMedical(rendezVousDto.getServiceMedical()); // Assuming ServiceMedical is a simple object or handled by DTO

        rendezVousToSave.setPatient(patient);
        rendezVousToSave.setMedecin(medecin);
        rendezVousToSave.setSalle(salle);

        // --- Save Entity ---
        RendezVous savedEntity = rendezVousRepository.save(rendezVousToSave);
        RendezVousDto savedRendezVousDto = RendezVousDto.fromEntity(savedEntity);

        // --- Historique Logging ---
        historiqueActionService.enregistrerAction(
                "Création du rendez-vous ID: " + savedRendezVousDto.getId() +
                        ", Patient: " + (savedRendezVousDto.getPatientSummary() != null && savedRendezVousDto.getPatientSummary().getInfoPersonnel() != null ? savedRendezVousDto.getPatientSummary().getInfoPersonnel().getNom() + " " + savedRendezVousDto.getPatientSummary().getInfoPersonnel().getPrenom() : "N/A") +
                        ", Médecin: " + (savedRendezVousDto.getMedecinSummary() != null && savedRendezVousDto.getMedecinSummary().getInfoPersonnel() != null ? savedRendezVousDto.getMedecinSummary().getInfoPersonnel().getNom() : "N/A") +
                        ", Salle: " + (savedRendezVousDto.getSalleSummary() != null ? savedRendezVousDto.getSalleSummary().getNumero() : "N/A") +
                        ", Date: " + savedRendezVousDto.getJour() + ", Heure: " + savedRendezVousDto.getHeure()
        );
        return savedRendezVousDto;
    }



    @Override
    @Transactional() // Mark as read-only as it only retrieves data
    public RendezVousDto findRendezVousById(Integer id) {
        RendezVous foundRendezVous = rendezVousRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rendez-vous non trouvé avec l'ID: " + id));

        RendezVousDto foundRendezVousDto = RendezVousDto.fromEntity(foundRendezVous);

        historiqueActionService.enregistrerAction(
                "Recherche du rendez-vous ID: " + id +
                        ", Patient: " + (foundRendezVousDto.getPatientSummary() != null && foundRendezVousDto.getPatientSummary().getInfoPersonnel() != null ? foundRendezVousDto.getPatientSummary().getInfoPersonnel().getNom() : "N/A") +
                        ", Date: " + foundRendezVousDto.getJour() + ", Heure: " + foundRendezVousDto.getHeure()
        );
        return foundRendezVousDto;
    }



    @Override
    @Transactional() // Mark as read-only
    public List<RendezVousDto> findAllRendezVous() {
        List<RendezVousDto> allRendezVous = rendezVousRepository.findAll().stream()
                .map(RendezVousDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction(
                "Affichage de tous les rendez-vous (nombre de résultats: " + allRendezVous.size() + ")"
        );
        return allRendezVous;
    }



    @Override
    @Transactional
    public RendezVousDto updateRendezVous(Integer id, RendezVousDto rendezVousDto) {
        RendezVous existingRendezVous = rendezVousRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rendez-vous non trouvé avec l'ID: " + id));

        // Store old values for logging
        LocalDate oldJour = existingRendezVous.getJour();
        LocalTime oldHeure = existingRendezVous.getHeure();
        StatutRDV oldStatut = existingRendezVous.getStatut();
        Integer oldPatientId = existingRendezVous.getPatient() != null ? existingRendezVous.getPatient().getId() : null;
        Integer oldMedecinId = existingRendezVous.getMedecin() != null ? existingRendezVous.getMedecin().getId() : null;
        Integer oldSalleId = existingRendezVous.getSalle() != null ? existingRendezVous.getSalle().getId() : null;

        // --- Update Fields and Relationships ---
        boolean timeOrResourceChanged = false;

        if (rendezVousDto.getJour() != null && !rendezVousDto.getJour().equals(oldJour)) {
            existingRendezVous.setJour(rendezVousDto.getJour());
            timeOrResourceChanged = true;
        }
        if (rendezVousDto.getHeure() != null && !rendezVousDto.getHeure().equals(oldHeure)) {
            existingRendezVous.setHeure(rendezVousDto.getHeure());
            timeOrResourceChanged = true;
        }
        if (rendezVousDto.getStatut() != null && !rendezVousDto.getStatut().equals(oldStatut)) {
            existingRendezVous.setStatut(rendezVousDto.getStatut());
        }
        if (rendezVousDto.getNotes() != null) {
            existingRendezVous.setNotes(rendezVousDto.getNotes());
        }
        if (rendezVousDto.getServiceMedical() != null) {
            existingRendezVous.setServiceMedical(rendezVousDto.getServiceMedical());
        }

        // Update relationships if IDs are provided and different
        if (rendezVousDto.getPatientSummary() != null && rendezVousDto.getPatientSummary().getId() != null && !rendezVousDto.getPatientSummary().getId().equals(oldPatientId)) {
            Patient patient = patientRepository.findById(rendezVousDto.getPatientSummary().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Nouveau patient non trouvé avec l'ID: " + rendezVousDto.getPatientSummary().getId()));
            existingRendezVous.setPatient(patient);
            // Consider if patient change affects availability - usually not, but keep in mind
        }
        if (rendezVousDto.getMedecinSummary() != null && rendezVousDto.getMedecinSummary().getId() != null && !rendezVousDto.getMedecinSummary().getId().equals(oldMedecinId)) {
            Utilisateur medecin = utilisateurRepository.findById(rendezVousDto.getMedecinSummary().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Nouveau médecin non trouvé avec l'ID: " + rendezVousDto.getMedecinSummary().getId()));
            existingRendezVous.setMedecin(medecin);
            timeOrResourceChanged = true; // Medecin changed, re-check availability
        }
        if (rendezVousDto.getSalleSummary() != null && rendezVousDto.getSalleSummary().getId() != null && !rendezVousDto.getSalleSummary().getId().equals(oldSalleId)) {
            Salle salle = salleRepository.findById(rendezVousDto.getSalleSummary().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Nouvelle salle non trouvée avec l'ID: " + rendezVousDto.getSalleSummary().getId()));
            existingRendezVous.setSalle(salle);
            timeOrResourceChanged = true; // Salle changed, re-check availability
        }

        // If time or any resource changed, re-check availability (excluding the current rendezvous itself)
        if (timeOrResourceChanged) {
            boolean isAvailable = isRendezVousAvailableForUpdate(
                    existingRendezVous.getJour(),
                    existingRendezVous.getHeure(),
                    existingRendezVous.getMedecin(),
                    existingRendezVous.getSalle(),
                    existingRendezVous.getId() // Exclude the current rendezvous
            );
            if (!isAvailable) {
                throw new IllegalStateException("Le médecin ou la salle n'est pas disponible pour la nouvelle date/heure/ressource.");
            }
        }

        RendezVousDto updatedRendezVous = RendezVousDto.fromEntity(rendezVousRepository.save(existingRendezVous));

        // --- Historique Logging ---
        StringBuilder logMessage = new StringBuilder("Mise à jour du rendez-vous ID: " + id + ".");
        if (!Objects.equals(oldJour, updatedRendezVous.getJour())) logMessage.append(" Date: ").append(oldJour).append(" -> ").append(updatedRendezVous.getJour()).append(".");
        if (!Objects.equals(oldHeure, updatedRendezVous.getHeure())) logMessage.append(" Heure: ").append(oldHeure).append(" -> ").append(updatedRendezVous.getHeure()).append(".");
        if (!Objects.equals(oldStatut, updatedRendezVous.getStatut())) logMessage.append(" Statut: ").append(oldStatut).append(" -> ").append(updatedRendezVous.getStatut()).append(".");
        if (!Objects.equals(oldPatientId, (updatedRendezVous.getPatientSummary() != null ? updatedRendezVous.getPatientSummary().getId() : null))) logMessage.append(" Patient ID: ").append(oldPatientId).append(" -> ").append(updatedRendezVous.getPatientSummary() != null ? updatedRendezVous.getPatientSummary().getId() : "N/A").append(".");
        if (!Objects.equals(oldMedecinId, (updatedRendezVous.getMedecinSummary() != null ? updatedRendezVous.getMedecinSummary().getId() : null))) logMessage.append(" Médecin ID: ").append(oldMedecinId).append(" -> ").append(updatedRendezVous.getMedecinSummary() != null ? updatedRendezVous.getMedecinSummary().getId() : "N/A").append(".");
        if (!Objects.equals(oldSalleId, (updatedRendezVous.getSalleSummary() != null ? updatedRendezVous.getSalleSummary().getId() : null))) logMessage.append(" Salle ID: ").append(oldSalleId).append(" -> ").append(updatedRendezVous.getSalleSummary() != null ? updatedRendezVous.getSalleSummary().getId() : "N/A").append(".");

        historiqueActionService.enregistrerAction(logMessage.toString());
        return updatedRendezVous;
    }



    @Override
    @Transactional
    public void deleteRendezVous(Integer id) {
        RendezVous rendezVousToDelete = rendezVousRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rendez-vous non trouvé avec l'ID: " + id + " et ne peut pas être supprimé."));

        // Check for associated consultations. If a RendezVous has a Consultation, it should probably not be deleted.
        // Assuming Consultation has a @OneToOne relationship with RendezVous (owning side):
        if (rendezVousToDelete.getConsultation() != null) {
            historiqueActionService.enregistrerAction(
                    "Tentative de suppression du rendez-vous ID: " + id + ": échec, car associé à la consultation ID: " + rendezVousToDelete.getConsultation().getId() + "."
            );
            throw new IllegalStateException("Impossible de supprimer ce rendez-vous car il est associé à une consultation existante. Veuillez supprimer la consultation d'abord si nécessaire.");
        }

        rendezVousRepository.deleteById(id);

        historiqueActionService.enregistrerAction(
                "Suppression du rendez-vous ID: " + id + ", Date: " + rendezVousToDelete.getJour() + ", Heure: " + rendezVousToDelete.getHeure()
        );
    }



    @Override
    @Transactional()
    public List<RendezVousDto> findRendezVousByPatientId(Integer patientId) {
        if (patientId == null) {
            throw new IllegalArgumentException("L'ID du patient ne peut pas être nul.");
        }
        // Validate patient existence
        patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient non trouvé avec l'ID: " + patientId));

        List<RendezVousDto> rendezVousList = rendezVousRepository.findByPatientId(patientId).stream()
                .map(RendezVousDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction(
                "Recherche de rendez-vous pour le patient ID: " + patientId + " (nombre de résultats: " + rendezVousList.size() + ")"
        );
        return rendezVousList;
    }



    @Override
    @Transactional()
    public List<RendezVousDto> findRendezVousByMedecinId(Integer medecinId) {
        if (medecinId == null) {
            throw new IllegalArgumentException("L'ID du médecin ne peut pas être nul.");
        }
        // Validate medecin existence
        utilisateurRepository.findById(medecinId)
                .orElseThrow(() -> new EntityNotFoundException("Médecin non trouvé avec l'ID: " + medecinId));

        List<RendezVousDto> rendezVousList = rendezVousRepository.findByMedecinId(medecinId).stream()
                .map(RendezVousDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction(
                "Recherche de rendez-vous pour le médecin ID: " + medecinId + " (nombre de résultats: " + rendezVousList.size() + ")"
        );
        return rendezVousList;
    }



    @Override
    @Transactional()
    public List<RendezVousDto> findRendezVousBySalleId(Integer salleId) {
        if (salleId == null) {
            throw new IllegalArgumentException("L'ID de la salle ne peut pas être nul.");
        }
        // Validate salle existence
        salleRepository.findById(salleId)
                .orElseThrow(() -> new EntityNotFoundException("Salle non trouvée avec l'ID: " + salleId));

        List<RendezVousDto> rendezVousList = rendezVousRepository.findBySalleId(salleId).stream()
                .map(RendezVousDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction(
                "Recherche de rendez-vous pour la salle ID: " + salleId + " (nombre de résultats: " + rendezVousList.size() + ")"
        );
        return rendezVousList;
    }



    @Override
    @Transactional()
    public List<RendezVousDto> findRendezVousByJour(LocalDate jour) {
        if (jour == null) {
            throw new IllegalArgumentException("La date ne peut pas être nulle.");
        }
        List<RendezVousDto> rendezVousList = rendezVousRepository.findByJour(jour).stream()
                .map(RendezVousDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction(
                "Recherche de rendez-vous pour le jour: " + jour + " (nombre de résultats: " + rendezVousList.size() + ")"
        );
        return rendezVousList;
    }



    @Override
    @Transactional()
    public List<RendezVousDto> findRendezVousByStatut(StatutRDV statut) {
        if (statut == null) {
            throw new IllegalArgumentException("Le statut ne peut pas être nul.");
        }
        List<RendezVousDto> rendezVousList = rendezVousRepository.findByStatut(statut).stream()
                .map(RendezVousDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction(
                "Recherche de rendez-vous avec le statut: " + statut + " (nombre de résultats: " + rendezVousList.size() + ")"
        );
        return rendezVousList;
    }



    @Override
    @Transactional
    public RendezVousDto cancelRendezVous(Integer id) {
        RendezVous rendezVous = rendezVousRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rendez-vous non trouvé avec l'ID: " + id));

        if (rendezVous.getStatut() == StatutRDV.ANNULE || rendezVous.getStatut() == StatutRDV.TERMINE) {
            throw new IllegalStateException("Impossible d'annuler un rendez-vous déjà annulé ou terminé.");
        }

        StatutRDV oldStatut = rendezVous.getStatut(); // Capture old status for logging
        rendezVous.setStatut(StatutRDV.ANNULE);
        RendezVousDto updatedRendezVous = RendezVousDto.fromEntity(rendezVousRepository.save(rendezVous));

        historiqueActionService.enregistrerAction(
                "Annulation du rendez-vous ID: " + id + ", Ancien statut: " + oldStatut + " -> Nouveau statut: " + StatutRDV.ANNULE
        );
        return updatedRendezVous;
    }



    @Transactional() // This helper method should also be read-only
    public boolean isRendezVousAvailable(LocalDate jour, LocalTime heure, Utilisateur medecin, Salle salle) {
        // This method is for checking availability for a *new* rendezvous or when *not* excluding one.
        // It should check against all existing PLANIFIE and CONFIRME rendez-vous.

        // Check if the doctor is occupied
        boolean medecinOccupied = rendezVousRepository
                .findByMedecinAndJourAndHeureAndStatutIn(medecin, jour, heure, List.of(StatutRDV.PLANIFIE, StatutRDV.CONFIRME))
                .isPresent();

        if (medecinOccupied) {
            return false;
        }

        // Check if the room is occupied
        boolean salleOccupied = rendezVousRepository
                .findBySalleAndJourAndHeureAndStatutIn(salle, jour, heure, List.of(StatutRDV.PLANIFIE, StatutRDV.CONFIRME))
                .isPresent();

        if (salleOccupied) {
            return false;
        }

        return true;
    }



    @Transactional() // This helper method should also be read-only
    public boolean isRendezVousAvailableForUpdate(LocalDate jour, LocalTime heure, Utilisateur medecin, Salle salle, Integer rendezvousToExcludeId) {
        // This method is for checking availability when *updating* an existing rendezvous.
        // It should exclude the rendezvous being updated from the conflict check.

        // Check if the doctor is occupied by *another* rendezvous
        Optional<RendezVous> conflictingMedecinRdv = rendezVousRepository
                .findByMedecinAndJourAndHeureAndStatutIn(medecin, jour, heure, List.of(StatutRDV.PLANIFIE, StatutRDV.CONFIRME));

        if (conflictingMedecinRdv.isPresent() && !conflictingMedecinRdv.get().getId().equals(rendezvousToExcludeId)) {
            return false; // Doctor is occupied by a different rendezvous
        }

        // Check if the room is occupied by *another* rendezvous
        Optional<RendezVous> conflictingSalleRdv = rendezVousRepository
                .findBySalleAndJourAndHeureAndStatutIn(salle, jour, heure, List.of(StatutRDV.PLANIFIE, StatutRDV.CONFIRME));

        if (conflictingSalleRdv.isPresent() && !conflictingSalleRdv.get().getId().equals(rendezvousToExcludeId)) {
            return false; // Room is occupied by a different rendezvous
        }

        return true;
    }
}