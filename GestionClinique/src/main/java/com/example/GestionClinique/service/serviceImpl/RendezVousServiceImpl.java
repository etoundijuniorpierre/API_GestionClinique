package com.example.GestionClinique.service.serviceImpl;


import com.example.GestionClinique.dto.RendezVousDto;
import com.example.GestionClinique.model.entity.Patient;
import com.example.GestionClinique.model.entity.RendezVous;
import com.example.GestionClinique.model.entity.Salle;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import com.example.GestionClinique.model.entity.enumElem.StatutSalle;
import com.example.GestionClinique.repository.PatientRepository;
import com.example.GestionClinique.repository.RendezVousRepository;
import com.example.GestionClinique.repository.SalleRepository;
import com.example.GestionClinique.repository.UtilisateurRepository;
import com.example.GestionClinique.service.HistoriqueActionService; // Import HistoriqueActionService
import com.example.GestionClinique.service.RendezVousService;
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

@Service
public class RendezVousServiceImpl implements RendezVousService {
    private final RendezVousRepository rendezVousRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final SalleRepository salleRepository;
    private final PatientRepository patientRepository;
    private final HistoriqueActionService historiqueActionService; // Inject HistoriqueActionService

    @Autowired
    public RendezVousServiceImpl(RendezVousRepository rendezVousRepository,
                                 UtilisateurRepository utilisateurRepository,
                                 SalleRepository salleRepository,
                                 PatientRepository patientRepository,
                                 HistoriqueActionService historiqueActionService) { // Add to constructor
        this.rendezVousRepository = rendezVousRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.salleRepository = salleRepository;
        this.patientRepository = patientRepository;
        this.historiqueActionService = historiqueActionService; // Initialize
    }



    @Override
    @Transactional
    public RendezVousDto createRendezVous(RendezVousDto rendezVousDto) {

        if (rendezVousDto.getPatient() == null || rendezVousDto.getPatient().getId() == null) {
            throw new IllegalArgumentException("Un rendez-vous doit être associé à un patient.");
        }
        if (rendezVousDto.getMedecin() == null || rendezVousDto.getMedecin().getId() == null) {
            throw new IllegalArgumentException("Un rendez-vous doit être associé à un médecin.");
        }
        if (rendezVousDto.getSalle() == null || rendezVousDto.getSalle().getId() == null) {
            throw new IllegalArgumentException("Un rendez-vous doit être associé à une salle.");
        }
        if (rendezVousDto.getJour() == null) {
            throw new IllegalArgumentException("La date du rendez-vous (jour) est obligatoire.");
        }
        if (rendezVousDto.getHeure() == null) {
            throw new IllegalArgumentException("L'heure du rendez-vous est obligatoire.");
        }

        //vérifier patient
        Patient existingPatient = patientRepository.findById(rendezVousDto.getPatient().getId())
                .orElseThrow(() -> new EntityNotFoundException("le patient avec l'id " +rendezVousDto.getPatient().getId()+ " n'existe pas"));
        //vérifier medecin
        Utilisateur existingMedecin = utilisateurRepository.findById(rendezVousDto.getMedecin().getId())
                .orElseThrow(() -> new EntityNotFoundException("le medecin avec l'id "+rendezVousDto.getMedecin().getId()+" n'existe pas"));

        //vérifier salle
        Salle existingSalle = salleRepository.findById(rendezVousDto.getSalle().getId())
                .orElseThrow(() -> new EntityNotFoundException("la salle avec l'id "+rendezVousDto.getSalle().getId()+" n'est pas"));

        // on vérifie le statut de la salle (ATTENTION: le statut OCCUPEE n'est généralement pas pour la création, mais pour la vérification de disponibilité avant de passer à RESERVED)
        // Si vous voulez dire que la salle DOIT être DISPONIBLE pour pouvoir être réservée:
        if (existingSalle.getStatutSalle() != StatutSalle.DISPONIBLE) { // Changed condition
            throw new IllegalStateException("La salle avec l'ID " + existingSalle.getId() + " est " + existingSalle.getStatutSalle() + ". Veuillez choisir une autre salle.");
        }

        // Vérifier si le créneau horaire est déjà pris pour le médecin
        if (rendezVousRepository.findByMedecinAndJourAndHeure(existingMedecin, rendezVousDto.getJour(), rendezVousDto.getHeure()).isPresent()) {
            throw new IllegalStateException("Le médecin " + existingMedecin.getInfoPersonnel().getNom() + " est déjà occupé à cette date et heure.");
        }

        // Vérifier si le créneau horaire est déjà pris pour la salle (même si la salle est DISPONIBLE, un autre RDV pourrait la réserver)
        if (rendezVousRepository.findBySalleAndJourAndHeure(existingSalle, rendezVousDto.getJour(), rendezVousDto.getHeure()).isPresent()) {
            throw new IllegalStateException("La salle avec l'ID " + existingSalle.getId() + " est déjà réservée à cette date et heure.");
        }

        // Vérifier qu'un patient n'a pas déjà un rendez-vous au même moment (optionnel, selon la règle métier)
        if (rendezVousRepository.findByPatientAndJourAndHeure(existingPatient, rendezVousDto.getJour(), rendezVousDto.getHeure()).isPresent()) {
            throw new IllegalStateException("Le patient a déjà un autre rendez-vous à cette date et heure.");
        }

        RendezVous newRendezVous = RendezVousDto.toEntity(rendezVousDto);
        newRendezVous.setPatient(existingPatient);
        newRendezVous.setMedecin(existingMedecin);
        newRendezVous.setSalle(existingSalle);
        //modifier le statut du rendezVous
        newRendezVous.setStatut(StatutRDV.CONFIRME); // Statut par défaut pour un nouveau RDV créé

        RendezVousDto savedRendezVous = RendezVousDto.fromEntity(
                rendezVousRepository.save(
                        newRendezVous
                )
        );

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Création du rendez-vous ID: " + savedRendezVous.getId() +
                        " pour le patient " + existingPatient.getInfoPersonnel().getNom() +
                        " avec le médecin " + existingMedecin.getInfoPersonnel().getNom() +
                        " dans la salle " + existingSalle.getNumero() +
                        " le " + savedRendezVous.getJour() + " à " + savedRendezVous.getHeure()
        );
        // --- Fin de l'ajout de l'historique ---

        return savedRendezVous;
    }



    @Override
    @Transactional
    public RendezVousDto findRendezVousById(Integer id) {

        RendezVousDto foundRendezVous = rendezVousRepository.findById(id)
                .map(RendezVousDto::fromEntity)
                .orElseThrow(()-> new RuntimeException("rendezVous pas disponible"));

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Recherche du rendez-vous ID: " + id
        );
        // --- Fin de l'ajout de l'historique ---

        return foundRendezVous;
    }



    @Override
    @Transactional
    public RendezVousDto updateRendezVous(Integer rendezVousId, RendezVousDto rendezVousDto) {

        RendezVous existingRendezVous = rendezVousRepository.findById(rendezVousId)
                .orElseThrow(() -> new IllegalArgumentException("le rendezVous n'existe pas"));

        // Stocker l'état avant la mise à jour pour le logging
        LocalDate oldJour = existingRendezVous.getJour();
        LocalTime oldHeure = existingRendezVous.getHeure();
        StatutRDV oldStatut = existingRendezVous.getStatut();
        String oldNotes = existingRendezVous.getNotes();
        Integer oldPatientId = existingRendezVous.getPatient() != null ? existingRendezVous.getPatient().getId() : null;
        Integer oldMedecinId = existingRendezVous.getMedecin() != null ? existingRendezVous.getMedecin().getId() : null;
        Integer oldSalleId = existingRendezVous.getSalle() != null ? existingRendezVous.getSalle().getId() : null;


        // mise à jour heure et jour du rendezVous
        if (rendezVousDto.getHeure() != null) {
            existingRendezVous.setHeure(rendezVousDto.getHeure());
        }
        if (rendezVousDto.getJour() != null) {
            existingRendezVous.setJour(rendezVousDto.getJour());
        }

        //mettre à jour le statut et les notes
        if (rendezVousDto.getStatut() != null) {
            existingRendezVous.setStatut(rendezVousDto.getStatut());
        }
        if (rendezVousDto.getNotes() != null && !rendezVousDto.getNotes().trim().isEmpty()) {
            existingRendezVous.setNotes(rendezVousDto.getNotes());
        }

        // changer le serviceMedical
        if (rendezVousDto.getServiceMedical() != null) {
            existingRendezVous.setServiceMedical(rendezVousDto.getServiceMedical());
        }


        // changer le patient
        if (rendezVousDto.getPatient() != null && rendezVousDto.getPatient().getId() != null &&
                !Objects.equals(existingRendezVous.getPatient().getId(), rendezVousDto.getPatient().getId())) {
            Patient newPatient = patientRepository.findById(rendezVousDto.getPatient().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Le nouveau patient avec l'ID " + rendezVousDto.getPatient().getId() + " n'existe pas."));
            existingRendezVous.setPatient(newPatient);
        }

        // changer le medecien du rendezvous
        if (rendezVousDto.getMedecin() != null && rendezVousDto.getMedecin().getId() != null &&
                !Objects.equals(existingRendezVous.getMedecin().getId(), rendezVousDto.getMedecin().getId())) {
            Utilisateur newMedecin = utilisateurRepository.findById(rendezVousDto.getMedecin().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Le nouveau médecin avec l'ID " + rendezVousDto.getMedecin().getId() + " n'existe pas."));
            existingRendezVous.setMedecin(newMedecin);
        }

        //changer de salle
        if (rendezVousDto.getSalle() != null && rendezVousDto.getSalle().getId() != null &&
                !Objects.equals(existingRendezVous.getSalle().getId(), rendezVousDto.getSalle().getId())) {
            Salle newSalle = salleRepository.findById(rendezVousDto.getSalle().getId())
                    .orElseThrow(() -> new EntityNotFoundException("La nouvelle salle avec l'ID " + rendezVousDto.getSalle().getId() + " n'existe pas."));
            existingRendezVous.setSalle(newSalle);
        }


        RendezVousDto updatedRendezVous = RendezVousDto.fromEntity(
                rendezVousRepository.save(
                        existingRendezVous
                )
        );

        // --- Ajout de l'historique ---
        StringBuilder logMessage = new StringBuilder("Mise à jour du rendez-vous ID: " + rendezVousId + ".");
        if (!oldJour.equals(updatedRendezVous.getJour())) logMessage.append(" Jour: ").append(oldJour).append(" -> ").append(updatedRendezVous.getJour());
        if (!oldHeure.equals(updatedRendezVous.getHeure())) logMessage.append(" Heure: ").append(oldHeure).append(" -> ").append(updatedRendezVous.getHeure());
        if (!oldStatut.equals(updatedRendezVous.getStatut())) logMessage.append(" Statut: ").append(oldStatut).append(" -> ").append(updatedRendezVous.getStatut());
        if (!Objects.equals(oldNotes, updatedRendezVous.getNotes())) logMessage.append(" Notes mises à jour."); // Simplified
        if (!Objects.equals(oldPatientId, updatedRendezVous.getPatient().getId())) logMessage.append(" Patient ID: ").append(oldPatientId).append(" -> ").append(updatedRendezVous.getPatient().getId());
        if (!Objects.equals(oldMedecinId, updatedRendezVous.getMedecin().getId())) logMessage.append(" Médecin ID: ").append(oldMedecinId).append(" -> ").append(updatedRendezVous.getMedecin().getId());
        if (!Objects.equals(oldSalleId, updatedRendezVous.getSalle().getId())) logMessage.append(" Salle ID: ").append(oldSalleId).append(" -> ").append(updatedRendezVous.getSalle().getId());

        historiqueActionService.enregistrerAction(logMessage.toString());
        // --- Fin de l'ajout de l'historique ---

        return updatedRendezVous;
    }



    @Override
    @Transactional
    public void deleteRendezVous(Integer rendezVousId) {
        if (!rendezVousRepository.existsById(rendezVousId)) {
            throw new IllegalArgumentException("le rendezVous avec l'id "+rendezVousId+" n'existe pas");
        }
        rendezVousRepository.deleteById(rendezVousId);

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Suppression du rendez-vous ID: " + rendezVousId
        );
        // --- Fin de l'ajout de l'historique ---
    }



    @Override
    @Transactional
    public List<RendezVousDto> findAllRendezVous() {

        List<RendezVousDto> allRendezVous = rendezVousRepository.findAll()
                .stream()
                .filter(Objects::nonNull)
                .map(RendezVousDto::fromEntity)
                .collect(Collectors.toList());

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Affichage de tous les rendez-vous."
        );
        // --- Fin de l'ajout de l'historique ---

        return allRendezVous;
    }



    @Override
    @Transactional
    public List<RendezVousDto> findRendezVousByStatut(StatutRDV statut) {

        if (statut == null) {
            throw new IllegalArgumentException("Le statut ne peut pas être nul.");
        }

        List<RendezVousDto> rendezvousByStatut = rendezVousRepository.findRendezVousByStatut(statut)
                .stream()
                .filter(Objects::nonNull)
                .map(RendezVousDto::fromEntity)
                .collect(Collectors.toList());

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Recherche des rendez-vous avec le statut: " + statut + " (nombre de résultats: " + rendezvousByStatut.size() + ")"
        );
        // --- Fin de l'ajout de l'historique ---

        return rendezvousByStatut;
    }



    @Override
    @Transactional
    public List<RendezVousDto> findRendezVousBySalleId(Integer salleId) {

        Salle existingSalle = salleRepository.findById(salleId)
                .orElseThrow(() -> new IllegalArgumentException("la salle avec l'id "+salleId+" n'existe pas"));

        if (existingSalle.getRendezVous() == null) {
            return List.of();
        }

        List<RendezVousDto> rendezvousBySalle = existingSalle.getRendezVous()
                .stream()
                .filter(Objects::nonNull)
                .map(RendezVousDto::fromEntity)
                .collect(Collectors.toList());

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Recherche des rendez-vous pour la salle ID: " + salleId + " (nombre de résultats: " + rendezvousBySalle.size() + ")"
        );
        // --- Fin de l'ajout de l'historique ---

        return rendezvousBySalle;
    }



    @Override
    @Transactional
    public List<RendezVousDto> findRendezVousByPatientId(Integer patientId) {

        Patient existingPatient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("le patient avec l'id "+patientId+" n'existe pas"));

        if (existingPatient.getRendezVous() == null) {
            return List.of();
        }

        List<RendezVousDto> rendezvousByPatient = existingPatient.getRendezVous()
                .stream()
                .filter(Objects::nonNull)
                .map(RendezVousDto::fromEntity)
                .collect(Collectors.toList());

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Recherche des rendez-vous pour le patient ID: " + patientId + " (nombre de résultats: " + rendezvousByPatient.size() + ")"
        );
        // --- Fin de l'ajout de l'historique ---

        return rendezvousByPatient;
    }



    @Override
    @Transactional
    public List<RendezVousDto> findRendezVousByMedecinId(Integer utilisateurId) {

        Utilisateur existingUtilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() ->  new IllegalArgumentException("l'utilisateur avec l'id "+utilisateurId+" n'existe pas"));

        if (existingUtilisateur.getRendezVous() == null) {
            return List.of();
        }

        List<RendezVousDto> rendezvousByMedecin = existingUtilisateur.getRendezVous()
                .stream()
                .filter(Objects::nonNull)
                .map(RendezVousDto::fromEntity)
                .collect(Collectors.toList());

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Recherche des rendez-vous pour le médecin ID: " + utilisateurId + " (nombre de résultats: " + rendezvousByMedecin.size() + ")"
        );
        // --- Fin de l'ajout de l'historique ---

        return rendezvousByMedecin;
    }



    @Override
    @Transactional
    public boolean isRendezVousAvailable(LocalDateTime dateHeureDebut, Integer medecinId, Integer salleId) {
        // Logique pour vérifier si le créneau est disponible pour le médecin ET la salle
        // Retourne true si disponible, false sinon
        // --- 1. Validation des entrées ---
        if (dateHeureDebut == null) {
            throw new IllegalArgumentException("La date et l'heure de début du rendez-vous sont obligatoires.");
        }
        if (medecinId == null) {
            throw new IllegalArgumentException("L'ID du médecin est obligatoire.");
        }
        if (salleId == null) {
            throw new IllegalArgumentException("L'ID de la salle est obligatoire.");
        }

        Utilisateur medecin = utilisateurRepository.findById(medecinId)
                .orElseThrow(() -> new IllegalArgumentException("le medecin avec l'id "+medecinId+" n'existe pas"));

        Salle salle = salleRepository.findById(salleId)
                .orElseThrow(() -> new IllegalArgumentException("la salle avec l'id "+salleId+" n'est pas"));

        // A faire si vous voulez que la salle soit DISPONIBLE pour être vérifiée.
        // Si une salle est déjà en statut OCCUPEE avant même la réservation, cela peut signifier qu'elle est déjà prise.
        // La logique ci-dessous vérifie la disponibilité par rapport aux RDV existants.
        // si (salle.getStatutSalle() != StatutSalle.DISPONIBLE) { return false; }


        LocalDate jour = dateHeureDebut.toLocalDate();
        LocalTime heure = dateHeureDebut.toLocalTime();

        // Vérifier si le médecin est déjà occupé à ce créneau
        boolean medecinOccupe = rendezVousRepository.findByMedecinAndJourAndHeure(medecin, jour, heure).isPresent();
        if (medecinOccupe) {
            historiqueActionService.enregistrerAction(
                    "Vérification disponibilité : Médecin ID " + medecinId + " est occupé le " + jour + " à " + heure
            );
            return false;
        }

        // Vérifier si la salle est déjà réservée à ce créneau (même si son statut général est DISPONIBLE)
        boolean salleReservee = rendezVousRepository.findBySalleAndJourAndHeure(salle, jour, heure).isPresent();
        if (salleReservee) {
            historiqueActionService.enregistrerAction(
                    "Vérification disponibilité : Salle ID " + salleId + " est déjà réservée le " + jour + " à " + heure
            );
            return false;
        }

        // Si toutes les vérifications passent, le créneau est disponible
        historiqueActionService.enregistrerAction(
                "Vérification disponibilité : Créneau disponible pour Médecin ID " + medecinId + " et Salle ID " + salleId + " le " + jour + " à " + heure
        );
        return true;
    }



    @Override
    @Transactional
    public RendezVousDto cancelRendezVous(Integer rendezVousId) {

        // Trouver le RendezVous par rendezVousId
        RendezVous rendezVous = rendezVousRepository.findById(rendezVousId)
                .orElseThrow(() -> new IllegalArgumentException("le rendezVous n'existe pas"));

        // Vérification de la logique d'annulation : Annulation possible uniquement si le RDV est Futur et avec un préavis suffisant.
        LocalDateTime dateHeureRdv = LocalDateTime.of(rendezVous.getJour(), rendezVous.getHeure());

        // Si le rendez-vous est déjà annulé ou terminé, interdire une nouvelle annulation
        if (rendezVous.getStatut() == StatutRDV.ANNULE || rendezVous.getStatut() == StatutRDV.TERMINE) {
            historiqueActionService.enregistrerAction(
                    "Tentative d'annulation du rendez-vous ID " + rendezVousId + ": déjà annulé ou terminé."
            );
            throw new IllegalStateException("Le rendez-vous est déjà annulé ou terminé.");
        }

        // Condition d'annulation: au moins X heures avant le rendez-vous
        // J'ai modifié la logique pour être plus claire. La règle "24h après l'heure prévue..." semble être une règle d'auto-annulation/manqué, pas une condition pour l'annulation manuelle.
        final int MIN_PREAVIS_HEURES = 2; // Exemple: au moins 2 heures de préavis
        if (dateHeureRdv.isBefore(LocalDateTime.now().plusHours(MIN_PREAVIS_HEURES))) {
            historiqueActionService.enregistrerAction(
                    "Tentative d'annulation du rendez-vous ID " + rendezVousId + ": préavis insuffisant (moins de " + MIN_PREAVIS_HEURES + "h)."
            );
            throw new IllegalStateException("Impossible d'annuler le rendez-vous. Il est trop proche (" + dateHeureRdv.toLocalDate() + " à " + dateHeureRdv.toLocalTime() + "). Un préavis d'au moins " + MIN_PREAVIS_HEURES + " heures est requis.");
        }

        // Si la logique d'auto-annulation après 24h est une règle de fond et non une condition d'appel à `cancelRendezVous`:
        // if (dateHeureRdv.isBefore(LocalDateTime.now().minusHours(24)) && rendezVous.getStatut() != StatutRDV.TERMINE) {
        //     rendezVous.setStatut(StatutRDV.ANNULE); // Ou StatutRDV.MANQUE
        //     return RendezVousDto.fromEntity(rendezVousRepository.save(rendezVous));
        // }


        rendezVous.setStatut(StatutRDV.ANNULE);
        RendezVousDto canceledRendezVous = RendezVousDto.fromEntity(rendezVousRepository.save(rendezVous));

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Rendez-vous ID: " + rendezVousId + " annulé. (Date/Heure: " + rendezVous.getJour() + " " + rendezVous.getHeure() + ")"
        );
        // --- Fin de l'ajout de l'historique ---

        return canceledRendezVous;
    }
}