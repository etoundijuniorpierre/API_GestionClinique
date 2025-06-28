package com.example.GestionClinique.service.serviceImpl;

import com.example.GestionClinique.model.entity.Patient;
import com.example.GestionClinique.model.entity.RendezVous;
import com.example.GestionClinique.model.entity.Salle;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import com.example.GestionClinique.repository.PatientRepository;
import com.example.GestionClinique.repository.RendezVousRepository;
import com.example.GestionClinique.repository.SalleRepository;
import com.example.GestionClinique.repository.UtilisateurRepository;
import com.example.GestionClinique.service.RendezVousService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.ConfigurationException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RendezVousServiceImpl implements RendezVousService {

    private final RendezVousRepository rendezVousRepository;
    private final PatientRepository patientRepository;
    private final UtilisateurRepository utilisateurRepository; // For doctors
    private final SalleRepository salleRepository;

    @Autowired
    public RendezVousServiceImpl(RendezVousRepository rendezVousRepository,
                                 PatientRepository patientRepository,
                                 UtilisateurRepository utilisateurRepository,
                                 SalleRepository salleRepository) {
        this.rendezVousRepository = rendezVousRepository;
        this.patientRepository = patientRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.salleRepository = salleRepository;
    }

    @Override
    public RendezVous createRendezVous(RendezVous rendezVous) {
        // Fetch and set associated entities (Patient, Medecin, Salle)
        Patient patient = patientRepository.findById(rendezVous.getPatient().getId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + rendezVous.getPatient().getId()));
        Utilisateur medecin = utilisateurRepository.findById(Math.toIntExact(rendezVous.getMedecin().getId()))
                .orElseThrow(() -> new IllegalArgumentException("Medecin not found with ID: " + rendezVous.getMedecin().getId()));
        Salle salle = salleRepository.findById(Math.toIntExact(rendezVous.getSalle().getId()))
                .orElseThrow(() -> new IllegalArgumentException("Salle not found with ID: " + rendezVous.getSalle().getId()));

        rendezVous.setPatient(patient);
        rendezVous.setMedecin(medecin);
        rendezVous.setSalle(salle);


        if (!isRendezVousAvailable(rendezVous.getJour(), rendezVous.getHeure(), medecin.getId(), salle.getId())) {
            throw new RuntimeException("Le créneau horaire est déjà pris pour ce médecin ou cette salle.");
        }

     
        if (rendezVous.getStatut() == null) {
            rendezVous.setStatut(StatutRDV.PLANIFIE); 
        }

        return rendezVousRepository.save(rendezVous);
    }


    @Override
    @Transactional(readOnly = true)
    public RendezVous findRendezVousById(Long id) {
        return rendezVousRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("RendezVous not found with ID: " + id));
    }

    @Override
    public RendezVous updateRendezVous(Long id, RendezVous rendezVousDetails) {
        RendezVous existingRendezVous = rendezVousRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("RendezVous not found with ID: " + id));

        // Update basic fields
        existingRendezVous.setJour(rendezVousDetails.getJour());
        existingRendezVous.setHeure(rendezVousDetails.getHeure());
        existingRendezVous.setStatut(rendezVousDetails.getStatut());
        existingRendezVous.setNotes(rendezVousDetails.getNotes());
        existingRendezVous.setServiceMedical(rendezVousDetails.getServiceMedical());

        // Update associated entities if provided and changed
        if (rendezVousDetails.getPatient() != null && !rendezVousDetails.getPatient().getId().equals(existingRendezVous.getPatient().getId())) {
            Patient newPatient = patientRepository.findById(rendezVousDetails.getPatient().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + rendezVousDetails.getPatient().getId()));
            existingRendezVous.setPatient(newPatient);
        }
        if (rendezVousDetails.getMedecin() != null && !rendezVousDetails.getMedecin().getId().equals(existingRendezVous.getMedecin().getId())) {
            Utilisateur newMedecin = utilisateurRepository.findById(Math.toIntExact(rendezVousDetails.getMedecin().getId()))
                    .orElseThrow(() -> new IllegalArgumentException("Medecin not found with ID: " + rendezVousDetails.getMedecin().getId()));
            existingRendezVous.setMedecin(newMedecin);
        }
        if (rendezVousDetails.getSalle() != null && !rendezVousDetails.getSalle().getId().equals(existingRendezVous.getSalle().getId())) {
            Salle newSalle = salleRepository.findById(Math.toIntExact(rendezVousDetails.getSalle().getId()))
                    .orElseThrow(() -> new IllegalArgumentException("Salle not found with ID: " + rendezVousDetails.getSalle().getId()));
            existingRendezVous.setSalle(newSalle);
        }

        if (!isRendezVousAvailableForUpdate(existingRendezVous.getId(), existingRendezVous.getJour(), existingRendezVous.getHeure(),
                existingRendezVous.getMedecin().getId(), existingRendezVous.getSalle().getId())) {
            throw new RuntimeException("Le créneau horaire est déjà pris pour ce médecin ou cette salle.");
        }

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
        rendezVousRepository.delete(rendezVous);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> findAllRendezVous() {
        return rendezVousRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> findRendezVousByStatut(StatutRDV statut) {
        return rendezVousRepository.findByStatut(statut);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> findRendezVousBySalleId(Long salleId) {
        return rendezVousRepository.findBySalleId(salleId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> findRendezVousByPatientId(Long patientId) {
        return rendezVousRepository.findByPatientId(patientId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> findRendezVousByMedecinId(Long medecinId) {
        return rendezVousRepository.findByMedecinId(medecinId);
    }

    // Helper method to check availability for creation
    @Override
    @Transactional(readOnly = true)
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



    @Transactional(readOnly = true)
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

        if (rendezVous.getStatut() == StatutRDV.ANNULE || rendezVous.getStatut() == StatutRDV.CONFIRME) {
            throw new IllegalStateException("Cannot cancel a rendez-vous that is already " + rendezVous.getStatut().name().toLowerCase() + ".");
        }
        if (rendezVous.getJour().isBefore(LocalDate.now()) || (rendezVous.getJour().isEqual(LocalDate.now()) && rendezVous.getHeure().isBefore(LocalTime.now()))) {
            throw new IllegalStateException("Cannot cancel a past rendez-vous.");
        }

        rendezVous.setStatut(StatutRDV.CONFIRME);
        return rendezVousRepository.save(rendezVous);
    }


    @Override
    @Transactional(readOnly = true)
    public List<RendezVous> findRendezVousByJour(LocalDate jour) {
        return rendezVousRepository.findByJour(jour);
    }


}