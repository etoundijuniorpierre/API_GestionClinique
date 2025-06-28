package com.example.GestionClinique.service.serviceImpl;

import com.example.GestionClinique.dto.RequestDto.RendezVousRequestDto;
import com.example.GestionClinique.model.entity.Patient;
import com.example.GestionClinique.model.entity.RendezVous;
import com.example.GestionClinique.model.entity.Salle;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
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
    public RendezVousRequestDto createRendezVous(RendezVousRequestDto rendezVousRequestDto) {
        // --- Input Validation ---
        if (rendezVousRequestDto.getJour() == null || rendezVousRequestDto.getHeure() == null) {
            throw new IllegalArgumentException("La date et l'heure du rendez-vous sont obligatoires.");
        }
        // Validate IDs from Summary DTOs
        if (rendezVousRequestDto.getPatientId() == null || rendezVousRequestDto.getPatientId().getId() == null ||
                rendezVousRequestDto.getMedecinId() == null || rendezVousRequestDto.getMedecinId().getId() == null ||
                rendezVousRequestDto.getSalleId() == null || rendezVousRequestDto.getSalleId().getId() == null) {
            throw new IllegalArgumentException("Le patient, le médecin et la salle sont obligatoires pour un rendez-vous (IDs manquants).");
        }

        // Ensure default status if not provided
        if (rendezVousRequestDto.getStatut() == null) {
            rendezVousRequestDto.setStatut(StatutRDV.PLANIFIE);
        }

        // --- Fetch Related Entities ---
        Patient patient = patientRepository.findById(rendezVousRequestDto.getPatientId().getId())
                .orElseThrow(() -> new EntityNotFoundException("Patient non trouvé avec l'ID: " + rendezVousRequestDto.getPatientId().getId()));
        Utilisateur medecin = utilisateurRepository.findById(rendezVousRequestDto.getMedecinId().getId())
                .orElseThrow(() -> new EntityNotFoundException("Médecin non trouvé avec l'ID: " + rendezVousRequestDto.getMedecinId().getId()));
        Salle salle = salleRepository.findById(rendezVousRequestDto.getSalleId().getId())
                .orElseThrow(() -> new EntityNotFoundException("Salle non trouvée avec l'ID: " + rendezVousRequestDto.getSalleId().getId()));

        // --- Check for Availability (using the dedicated helper method) ---
        if (!isRendezVousAvailable(rendezVousRequestDto.getJour(), rendezVousRequestDto.getHeure(), medecin, salle)) {
            throw new IllegalStateException("Le médecin ou la salle n'est pas disponible à cette heure.");
        }

        // --- DTO to Entity Conversion & Relationship Setting ---
        RendezVous rendezVousToSave = new RendezVous(); // Manually create entity
        rendezVousToSave.setJour(rendezVousRequestDto.getJour());
        rendezVousToSave.setHeure(rendezVousRequestDto.getHeure());
        rendezVousToSave.setStatut(rendezVousRequestDto.getStatut());
        rendezVousToSave.setNotes(rendezVousRequestDto.getNotes());
        rendezVousToSave.setServiceMedical(rendezVousRequestDto.getServiceMedical()); // Assuming ServiceMedical is a simple object or handled by DTO

        rendezVousToSave.setPatient(patient);
        rendezVousToSave.setMedecin(medecin);
        rendezVousToSave.setSalle(salle);

        // --- Save Entity ---
        RendezVous savedEntity = rendezVousRepository.save(rendezVousToSave);
        RendezVousRequestDto savedRendezVousRequestDto = RendezVousRequestDto.fromEntity(savedEntity);

        // --- Historique Logging ---
        historiqueActionService.enregistrerAction(
                "Création du rendez-vous ID: " + savedRendezVousRequestDto.getId() +
                        ", Patient: " + (savedRendezVousRequestDto.getPatientId() != null && savedRendezVousRequestDto.getPatientId().getInfoPersonnel() != null ? savedRendezVousRequestDto.getPatientId().getInfoPersonnel().getNom() + " " + savedRendezVousRequestDto.getPatientId().getInfoPersonnel().getPrenom() : "N/A") +
                        ", Médecin: " + (savedRendezVousRequestDto.getMedecinId() != null && savedRendezVousRequestDto.getMedecinId().getInfoPersonnel() != null ? savedRendezVousRequestDto.getMedecinId().getInfoPersonnel().getNom() : "N/A") +
                        ", Salle: " + (savedRendezVousRequestDto.getSalleId() != null ? savedRendezVousRequestDto.getSalleId().getNumero() : "N/A") +
                        ", Date: " + savedRendezVousRequestDto.getJour() + ", Heure: " + savedRendezVousRequestDto.getHeure()
        );
        return savedRendezVousRequestDto;
    }



    @Override
    @Transactional() // Mark as read-only as it only retrieves data
    public RendezVousRequestDto findRendezVousById(Integer id) {
        RendezVous foundRendezVous = rendezVousRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rendez-vous non trouvé avec l'ID: " + id));

        RendezVousRequestDto foundRendezVousRequestDto = RendezVousRequestDto.fromEntity(foundRendezVous);

        historiqueActionService.enregistrerAction(
                "Recherche du rendez-vous ID: " + id +
                        ", Patient: " + (foundRendezVousRequestDto.getPatientId() != null && foundRendezVousRequestDto.getPatientId().getInfoPersonnel() != null ? foundRendezVousRequestDto.getPatientId().getInfoPersonnel().getNom() : "N/A") +
                        ", Date: " + foundRendezVousRequestDto.getJour() + ", Heure: " + foundRendezVousRequestDto.getHeure()
        );
        return foundRendezVousRequestDto;
    }



    @Override
    @Transactional() // Mark as read-only
    public List<RendezVousRequestDto> findAllRendezVous() {
        List<RendezVousRequestDto> allRendezVous = rendezVousRepository.findAll().stream()
                .map(RendezVousRequestDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction(
                "Affichage de tous les rendez-vous (nombre de résultats: " + allRendezVous.size() + ")"
        );
        return allRendezVous;
    }



    @Override
    @Transactional
    public RendezVousRequestDto updateRendezVous(Integer id, RendezVousRequestDto rendezVousRequestDto) {
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

        if (rendezVousRequestDto.getJour() != null && !rendezVousRequestDto.getJour().equals(oldJour)) {
            existingRendezVous.setJour(rendezVousRequestDto.getJour());
            timeOrResourceChanged = true;
        }
        if (rendezVousRequestDto.getHeure() != null && !rendezVousRequestDto.getHeure().equals(oldHeure)) {
            existingRendezVous.setHeure(rendezVousRequestDto.getHeure());
            timeOrResourceChanged = true;
        }
        if (rendezVousRequestDto.getStatut() != null && !rendezVousRequestDto.getStatut().equals(oldStatut)) {
            existingRendezVous.setStatut(rendezVousRequestDto.getStatut());
        }
        if (rendezVousRequestDto.getNotes() != null) {
            existingRendezVous.setNotes(rendezVousRequestDto.getNotes());
        }
        if (rendezVousRequestDto.getServiceMedical() != null) {
            existingRendezVous.setServiceMedical(rendezVousRequestDto.getServiceMedical());
        }

        // Update relationships if IDs are provided and different
        if (rendezVousRequestDto.getPatientId() != null && rendezVousRequestDto.getPatientId().getId() != null && !rendezVousRequestDto.getPatientId().getId().equals(oldPatientId)) {
            Patient patient = patientRepository.findById(rendezVousRequestDto.getPatientId().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Nouveau patient non trouvé avec l'ID: " + rendezVousRequestDto.getPatientId().getId()));
            existingRendezVous.setPatient(patient);
            // Consider if patient change affects availability - usually not, but keep in mind
        }
        if (rendezVousRequestDto.getMedecinId() != null && rendezVousRequestDto.getMedecinId().getId() != null && !rendezVousRequestDto.getMedecinId().getId().equals(oldMedecinId)) {
            Utilisateur medecin = utilisateurRepository.findById(rendezVousRequestDto.getMedecinId().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Nouveau médecin non trouvé avec l'ID: " + rendezVousRequestDto.getMedecinId().getId()));
            existingRendezVous.setMedecin(medecin);
            timeOrResourceChanged = true; // Medecin changed, re-check availability
        }
        if (rendezVousRequestDto.getSalleId() != null && rendezVousRequestDto.getSalleId().getId() != null && !rendezVousRequestDto.getSalleId().getId().equals(oldSalleId)) {
            Salle salle = salleRepository.findById(rendezVousRequestDto.getSalleId().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Nouvelle salle non trouvée avec l'ID: " + rendezVousRequestDto.getSalleId().getId()));
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

        RendezVousRequestDto updatedRendezVous = RendezVousRequestDto.fromEntity(rendezVousRepository.save(existingRendezVous));

        // --- Historique Logging ---
        StringBuilder logMessage = new StringBuilder("Mise à jour du rendez-vous ID: " + id + ".");
        if (!Objects.equals(oldJour, updatedRendezVous.getJour())) logMessage.append(" Date: ").append(oldJour).append(" -> ").append(updatedRendezVous.getJour()).append(".");
        if (!Objects.equals(oldHeure, updatedRendezVous.getHeure())) logMessage.append(" Heure: ").append(oldHeure).append(" -> ").append(updatedRendezVous.getHeure()).append(".");
        if (!Objects.equals(oldStatut, updatedRendezVous.getStatut())) logMessage.append(" Statut: ").append(oldStatut).append(" -> ").append(updatedRendezVous.getStatut()).append(".");
        if (!Objects.equals(oldPatientId, (updatedRendezVous.getPatientId() != null ? updatedRendezVous.getPatientId().getId() : null))) logMessage.append(" Patient ID: ").append(oldPatientId).append(" -> ").append(updatedRendezVous.getPatientId() != null ? updatedRendezVous.getPatientId().getId() : "N/A").append(".");
        if (!Objects.equals(oldMedecinId, (updatedRendezVous.getMedecinId() != null ? updatedRendezVous.getMedecinId().getId() : null))) logMessage.append(" Médecin ID: ").append(oldMedecinId).append(" -> ").append(updatedRendezVous.getMedecinId() != null ? updatedRendezVous.getMedecinId().getId() : "N/A").append(".");
        if (!Objects.equals(oldSalleId, (updatedRendezVous.getSalleId() != null ? updatedRendezVous.getSalleId().getId() : null))) logMessage.append(" Salle ID: ").append(oldSalleId).append(" -> ").append(updatedRendezVous.getSalleId() != null ? updatedRendezVous.getSalleId().getId() : "N/A").append(".");

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
    public List<RendezVousRequestDto> findRendezVousByPatientId(Integer patientId) {
        if (patientId == null) {
            throw new IllegalArgumentException("L'ID du patient ne peut pas être nul.");
        }
        // Validate patient existence
        patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("Patient non trouvé avec l'ID: " + patientId));

        List<RendezVousRequestDto> rendezVousList = rendezVousRepository.findByPatientId(patientId).stream()
                .map(RendezVousRequestDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction(
                "Recherche de rendez-vous pour le patient ID: " + patientId + " (nombre de résultats: " + rendezVousList.size() + ")"
        );
        return rendezVousList;
    }



    @Override
    @Transactional()
    public List<RendezVousRequestDto> findRendezVousByMedecinId(Integer medecinId) {
        if (medecinId == null) {
            throw new IllegalArgumentException("L'ID du médecin ne peut pas être nul.");
        }
        // Validate medecin existence
        utilisateurRepository.findById(medecinId)
                .orElseThrow(() -> new EntityNotFoundException("Médecin non trouvé avec l'ID: " + medecinId));

        List<RendezVousRequestDto> rendezVousList = rendezVousRepository.findByMedecinId(medecinId).stream()
                .map(RendezVousRequestDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction(
                "Recherche de rendez-vous pour le médecin ID: " + medecinId + " (nombre de résultats: " + rendezVousList.size() + ")"
        );
        return rendezVousList;
    }



    @Override
    @Transactional()
    public List<RendezVousRequestDto> findRendezVousBySalleId(Integer salleId) {
        if (salleId == null) {
            throw new IllegalArgumentException("L'ID de la salle ne peut pas être nul.");
        }
        // Validate salle existence
        salleRepository.findById(salleId)
                .orElseThrow(() -> new EntityNotFoundException("Salle non trouvée avec l'ID: " + salleId));

        List<RendezVousRequestDto> rendezVousList = rendezVousRepository.findBySalleId(salleId).stream()
                .map(RendezVousRequestDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction(
                "Recherche de rendez-vous pour la salle ID: " + salleId + " (nombre de résultats: " + rendezVousList.size() + ")"
        );
        return rendezVousList;
    }



    @Override
    @Transactional()
    public List<RendezVousRequestDto> findRendezVousByJour(LocalDate jour) {
        if (jour == null) {
            throw new IllegalArgumentException("La date ne peut pas être nulle.");
        }
        List<RendezVousRequestDto> rendezVousList = rendezVousRepository.findByJour(jour).stream()
                .map(RendezVousRequestDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction(
                "Recherche de rendez-vous pour le jour: " + jour + " (nombre de résultats: " + rendezVousList.size() + ")"
        );
        return rendezVousList;
    }



    @Override
    @Transactional()
    public List<RendezVousRequestDto> findRendezVousByStatut(StatutRDV statut) {
        if (statut == null) {
            throw new IllegalArgumentException("Le statut ne peut pas être nul.");
        }
        List<RendezVousRequestDto> rendezVousList = rendezVousRepository.findByStatut(statut).stream()
                .map(RendezVousRequestDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction(
                "Recherche de rendez-vous avec le statut: " + statut + " (nombre de résultats: " + rendezVousList.size() + ")"
        );
        return rendezVousList;
    }



    @Override
    @Transactional
    public RendezVousRequestDto cancelRendezVous(Integer id) {
        RendezVous rendezVous = rendezVousRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rendez-vous non trouvé avec l'ID: " + id));

        if (rendezVous.getStatut() == StatutRDV.ANNULE || rendezVous.getStatut() == StatutRDV.TERMINE) {
            throw new IllegalStateException("Impossible d'annuler un rendez-vous déjà annulé ou terminé.");
        }

        StatutRDV oldStatut = rendezVous.getStatut(); // Capture old status for logging
        rendezVous.setStatut(StatutRDV.ANNULE);
        RendezVousRequestDto updatedRendezVous = RendezVousRequestDto.fromEntity(rendezVousRepository.save(rendezVous));

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