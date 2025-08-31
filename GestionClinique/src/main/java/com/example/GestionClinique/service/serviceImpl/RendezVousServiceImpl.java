package com.example.GestionClinique.service.serviceImpl;

import com.example.GestionClinique.model.entity.Facture;
import com.example.GestionClinique.model.entity.RendezVous;
import com.example.GestionClinique.model.entity.Salle;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.StatutPaiement;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import com.example.GestionClinique.repository.*;
import com.example.GestionClinique.service.FactureService;
import com.example.GestionClinique.service.HistoriqueActionService;
import com.example.GestionClinique.service.RendezVousService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RendezVousServiceImpl implements RendezVousService {

    private final RendezVousRepository rendezVousRepository;
    private final UtilisateurRepository utilisateurRepository; // For doctors
    private final SalleRepository salleRepository;
    private final FactureService factureService;
    private final HistoriqueActionService historiqueActionService;
    private final LoggingAspect loggingAspect;
    private final FactureRepository factureRepository;


    @Override
    @Transactional
    public RendezVous createRendezVous(RendezVous rendezVous) {
        // Définir la salle en premier si serviceMedical est spécifié
        if (rendezVous.getServiceMedical() != null) {
            Salle salle = salleRepository.findByServiceMedical(rendezVous.getServiceMedical());
            rendezVous.setSalle(salle);
        }

        // Vérifier que la salle est bien définie
        if (rendezVous.getSalle() == null) {
            throw new IllegalArgumentException("La salle doit être spécifiée directement ou via le service médical");
        }

        // Maintenant vérifier la disponibilité
        if (!isRendezVousAvailable(
                rendezVous.getJour(),
                rendezVous.getHeure(),
                rendezVous.getMedecin().getId(),
                rendezVous.getSalle().getId())) {
            throw new RuntimeException("Le créneau horaire est déjà pris pour ce médecin ou cette salle.");
        }

        if (rendezVous.getStatut() == null) {
            rendezVous.setStatut(StatutRDV.EN_ATTENTE);
        }

        RendezVous saveRendezVous = rendezVousRepository.save(rendezVous);
        factureService.generateInvoiceForRendesVous(saveRendezVous.getId());

        historiqueActionService.enregistrerAction(
                String.format("Création RDV ID: %d pour patient ID: %d",
                        saveRendezVous.getId(), saveRendezVous.getPatient().getId()),
                loggingAspect.currentUserId()
        );

        return saveRendezVous;
    }


    @Override
    @Transactional
    public RendezVous findRendezVousById(Long id) {
        return rendezVousRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("RendezVous not found with ID: " + id));
    }

    @Override
    public RendezVous updateRendezVous(Long id, RendezVous rendezVous) {
        RendezVous existingRendezVous = rendezVousRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Rendez-vous non trouvé avec l'ID: " + id));

        // Valider que le rendez-vous peut être modifié
        if (existingRendezVous.getStatut() == StatutRDV.TERMINE || existingRendezVous.getStatut() == StatutRDV.ANNULE) {
            throw new IllegalStateException("Impossible de modifier un rendez-vous " + existingRendezVous.getStatut().name().toLowerCase());
        }

        // Mise à jour des champs modifiables
        if (rendezVous.getHeure() != null) {
            existingRendezVous.setHeure(rendezVous.getHeure());
        }
        if (rendezVous.getJour() != null) {
            existingRendezVous.setJour(rendezVous.getJour());
        }
        if (rendezVous.getNotes() != null) {
            existingRendezVous.setNotes(rendezVous.getNotes());
        }

        // Mise à jour du médecin si nécessaire
        if (rendezVous.getMedecin() != null && !rendezVous.getMedecin().equals(existingRendezVous.getMedecin().getId())) {
            Utilisateur newMedecin = utilisateurRepository.findById(rendezVous.getMedecin().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Médecin non trouvé avec l'ID: " + rendezVous.getMedecin()));
            existingRendezVous.setMedecin(newMedecin);
        }

        // Mise à jour du service médical et de la salle associée
        if (rendezVous.getServiceMedical() != null && !rendezVous.getServiceMedical().equals(existingRendezVous.getServiceMedical())) {
            existingRendezVous.setServiceMedical(rendezVous.getServiceMedical());
            Salle nouvelleSalle = salleRepository.findByServiceMedical(rendezVous.getServiceMedical());
            existingRendezVous.setSalle(nouvelleSalle);
        }

        // Vérification des conflits de planning
        if (!isRendezVousAvailableForUpdate(
                existingRendezVous.getId(),
                existingRendezVous.getJour(),
                existingRendezVous.getHeure(),
                existingRendezVous.getMedecin().getId(),
                existingRendezVous.getSalle().getId())) {
            throw new ConcurrentModificationException("Le créneau horaire est déjà pris pour ce médecin ou cette salle");
        }

        historiqueActionService.enregistrerAction(
                String.format("Mise à jour prescription ID: %d", id),
                loggingAspect.currentUserId()
        );

        return rendezVousRepository.save(existingRendezVous);
    }



    @Override
    public void deleteRendezVous(Long id) {
        RendezVous rendezVous = rendezVousRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("RendezVous not found with ID: " + id));
        // Business rule: Prevent deletion if consultation is already linked or if already occurred
        if (rendezVous.getConsultation() != null) {
            throw new IllegalStateException("Cannot delete a rendez-vous that already has an associated consultation.");
        }
        if (rendezVous.getJour().isBefore(LocalDate.now())) {
            throw new IllegalStateException("Cannot delete a past rendez-vous. Consider cancelling or archiving instead.");
        }
        historiqueActionService.enregistrerAction(
                String.format("Suppression rendezVous ID: %d", id),
                loggingAspect.currentUserId()
        );
        rendezVousRepository.delete(rendezVous);
    }

    @Override
    @Transactional
    public List<RendezVous> findAllRendezVous() {
        return rendezVousRepository.findAll();
    }

    @Override
    @Transactional
    public List<RendezVous> findRendezVousByStatut(StatutRDV statut) {
        return rendezVousRepository.findByStatut(statut);
    }

    @Override
    @Transactional
    public List<RendezVous> findRendezVousBySalleId(Long salleId) {
        return rendezVousRepository.findBySalleId(salleId);
    }


    // Helper method to check availability for creation
    @Override
    @Transactional
    public boolean isRendezVousAvailable(LocalDate jour, LocalTime heure, Long medecinId, Long salleId) {
        // Check if doctor is busy
        Optional<RendezVous> existingMedecinRv = rendezVousRepository.findByJourAndHeureAndMedecinId(jour, heure, medecinId);
        if (existingMedecinRv.isPresent()) {
            return false; // Doctor is busy
        }

        // Check if room is busy
        Optional<RendezVous> existingSalleRv = rendezVousRepository.findByJourAndHeureAndSalleId(jour, heure, salleId);
        if (existingSalleRv.isPresent()) {
            return false; // Room is busy
        }
        return true; // Both are available
    }



    @Transactional
    @Override
    public boolean isRendezVousAvailableForUpdate(Long rendezVousId, LocalDate jour, LocalTime heure, Long medecinId, Long salleId) {
        Optional<RendezVous> existingMedecinRv = rendezVousRepository.findByJourAndHeureAndMedecinId(jour, heure, medecinId);
        if (existingMedecinRv.isPresent() && !existingMedecinRv.get().getId().equals(rendezVousId)) {
            return false;
        }

        Optional<RendezVous> existingSalleRv = rendezVousRepository.findByJourAndHeureAndSalleId(jour, heure, salleId);
        if (existingSalleRv.isPresent() && !existingSalleRv.get().getId().equals(rendezVousId)) {
            return false;
        }
        return true;
    }


    @Override
    public RendezVous cancelRendezVous(Long rendezVousId) {
        RendezVous rendezVous = rendezVousRepository.findById(rendezVousId)
                .orElseThrow(() -> new IllegalArgumentException("RendezVous not found with ID: " + rendezVousId));

        if (rendezVous.getStatut() == StatutRDV.ANNULE || rendezVous.getStatut() == StatutRDV.ENCOURS) {
            throw new IllegalStateException("Cannot cancel a rendez-vous that is already " + rendezVous.getStatut().name().toLowerCase() + ".");
        }
        if (rendezVous.getJour().isBefore(LocalDate.now()) || (rendezVous.getJour().isEqual(LocalDate.now()) && rendezVous.getHeure().isBefore(LocalTime.now()))) {
            throw new IllegalStateException("Cannot cancel a past rendez-vous.");
        }

        rendezVous.setStatut(StatutRDV.ANNULE);
        RendezVous updatedRendezVous = rendezVousRepository.save(rendezVous);

        historiqueActionService.enregistrerAction(
                String.format("Annulation RDV ID: %d", rendezVousId),
                loggingAspect.currentUserId()
        );

        return updatedRendezVous;
    }


    @Override
    @Transactional
    public List<RendezVous> findRendezVousByJour(LocalDate jour) {
        return rendezVousRepository.findByJour(jour);
    }

    @Override
    @Transactional
    public void cancelRendezVousByJour() {
        LocalDate today = LocalDate.now();
        List<RendezVous> rendezVousList = rendezVousRepository.findByJourBefore(today);

        if (rendezVousList.isEmpty()) {
            return;
        }

        for (RendezVous rendezVous : rendezVousList) {
            if (rendezVous.getStatut() == StatutRDV.EN_ATTENTE) {
                // New logic to handle cancellation for past appointments
                rendezVous.setStatut(StatutRDV.ANNULE); // Set the status to 'canceled'
                rendezVousRepository.save(rendezVous); // Save the updated appointment

                Optional<Facture> factureOptional = factureRepository.findByRendezVousId(rendezVous.getId());
                factureOptional.ifPresent(facture -> {
                    if (facture.getStatutPaiement() == StatutPaiement.IMPAYEE) {
                        factureService.deleteFacture(facture.getId());
                    }
                });
            }
        }
    }

    @Override
    public List<RendezVous> findUtilisateurConfirmedRendezVousByMonth(Long idUtilisateur, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        return rendezVousRepository.findConfirmedByUtilisateurAndMonth(idUtilisateur, StatutRDV.CONFIRME, startDate, endDate);
    }

    @Override
    public List<RendezVous> findRendezVousByMonth(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        return rendezVousRepository.findByJourBetween(startDate, endDate);
    }

}