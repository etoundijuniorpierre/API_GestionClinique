package com.example.GestionClinique.service.serviceImpl;


import com.example.GestionClinique.model.entity.*;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import com.example.GestionClinique.model.entity.enumElem.StatutSalle;
import com.example.GestionClinique.repository.*;
import com.example.GestionClinique.service.ConsultationService;
import com.example.GestionClinique.service.FactureService;
import com.example.GestionClinique.service.HistoriqueActionService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.GestionClinique.model.entity.enumElem.StatutPaiement.PAYEE;


@Service
@AllArgsConstructor
public class ConsultationServiceImpl implements ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final DossierMedicalRepository dossierMedicalRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final RendezVousRepository rendezVousRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final SalleRepository salleRepository;
    private final FactureService factureService;
    private final HistoriqueActionService historiqueActionService;
    private final LoggingAspect loggingAspect;



    // This is for EMERGENCY consultations (no RendezVous)
    @Override
    @Transactional
    public Consultation createConsultation(Consultation consultation, Long medecinId) {

        Utilisateur medecin = utilisateurRepository.findById(medecinId)
                .orElseThrow(() -> new IllegalArgumentException("Medecin not found with ID: " + medecinId));
        consultation.setMedecin(medecin);
        consultation.setDossierMedical(null);
        consultation.getDossierMedical().setPatient(null);

        List<Prescription> savedPrescriptions = new ArrayList<>();
        if (consultation.getPrescriptions() != null && !consultation.getPrescriptions().isEmpty()) {
            if (consultation.getDossierMedical() != null && consultation.getDossierMedical().getPatient() != null) {
                for (Prescription prescription : consultation.getPrescriptions()) {
                    prescription.setConsultation(consultation);
                    prescription.setMedecin(medecin);
                    prescription.setPatient(consultation.getDossierMedical().getPatient());
                    prescription.setDossierMedical(consultation.getDossierMedical());
                    consultation.getDossierMedical().setDernierTraitement(prescription.getMedicaments());
                    savedPrescriptions.add(prescriptionRepository.save(prescription));
                }
                consultation.setPrescriptions(savedPrescriptions);
            } else {

                System.out.println("Warning: Prescriptions provided for emergency consultation without a linked DossierMedical/Patient. Prescriptions will not be saved.");
                consultation.setPrescriptions(new ArrayList<>());
            }
        }

        Consultation savedConsultation = consultationRepository.save(consultation);
        historiqueActionService.enregistrerAction(
                String.format("Création consultation d'urgence ID: %d par médecin ID: %d",
                        savedConsultation.getId(), medecinId),
                loggingAspect.currentUserId()
        );
        factureService.generateInvoiceForConsultation(savedConsultation.getId());

        return savedConsultation;
    }



    @Override
    @Transactional
    public Consultation startConsultation(Long rendezVousId, Consultation consultationDetails, Long medecinId) {
        RendezVous rendezVous = rendezVousRepository.findById(rendezVousId)
                .orElseThrow(() -> new IllegalArgumentException("RendezVous not found with ID: " + rendezVousId));

        if (rendezVous.getConsultation() != null) {
            throw new RuntimeException("RendezVous with ID " + rendezVousId + " is already linked to a consultation.");
        }

        if (rendezVous.getFacture().getStatutPaiement() != PAYEE) {
            throw new RuntimeException("RendezVous with ID " + rendezVousId + " is already linked to a consultation.");
        }

        rendezVous.setStatut(StatutRDV.ENCOURS);
        rendezVousRepository.save(rendezVous);

        Utilisateur medecin = utilisateurRepository.findById(medecinId)
                .orElseThrow(() -> new IllegalArgumentException("Medecin not found with ID: " + medecinId));
        consultationDetails.setMedecin(medecin);
        Salle salle = rendezVous.getSalle();

        if (salle != null) {
            salle.setStatutSalle(StatutSalle.OCCUPEE);
            salleRepository.save(salle);
        } else {
            throw new IllegalStateException("RendezVous does not have an associated room to mark as occupied.");
        }

        consultationDetails.setRendezVous(rendezVous);

        if (rendezVous.getPatient() != null && rendezVous.getPatient().getDossierMedical() != null) {
            consultationDetails.setDossierMedical(rendezVous.getPatient().getDossierMedical());
        } else {
            throw new RuntimeException("RendezVous patient does not have an associated medical record.");
        }


        if (consultationDetails.getPrescriptions() != null && !consultationDetails.getPrescriptions().isEmpty()) {
            for (Prescription prescription : consultationDetails.getPrescriptions()) {
                prescription.setConsultation(consultationDetails);
                prescription.setMedecin(medecin);
                prescription.setPatient(rendezVous.getPatient());
                prescription.setDossierMedical(rendezVous.getPatient().getDossierMedical());
                rendezVous.getPatient().getDossierMedical().setDernierTraitement(prescription.getMedicaments());
            }
        }

        Consultation newConsultation = consultationRepository.save(consultationDetails);

        rendezVous.setConsultation(newConsultation);
        rendezVousRepository.save(rendezVous);

        salle.setStatutSalle(StatutSalle.DISPONIBLE);
        rendezVous.setStatut(StatutRDV.TERMINE);
        salleRepository.save(salle);
        rendezVousRepository.save(rendezVous);

        historiqueActionService.enregistrerAction(
                String.format("Début consultation ID: %d pour rendez-vous ID: %d",
                        newConsultation.getId(), rendezVousId),
                loggingAspect.currentUserId()
        );

        return newConsultation;
    }



    @Override
    @Transactional
    public Consultation updateConsultation(Long id, Consultation consultationDetails) {
        Consultation existingConsultation = consultationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Consultation not found with ID: " + id));

        // Update basic fields
        existingConsultation.setMotifs(consultationDetails.getMotifs());
        existingConsultation.setTensionArterielle(consultationDetails.getTensionArterielle());
        existingConsultation.setTemperature(consultationDetails.getTemperature());
        existingConsultation.setPoids(consultationDetails.getPoids());
        existingConsultation.setTaille(consultationDetails.getTaille());
        existingConsultation.setCompteRendu(consultationDetails.getCompteRendu());
        existingConsultation.setDiagnostic(consultationDetails.getDiagnostic());

        historiqueActionService.enregistrerAction(
                String.format("Mise à jour consultation ID: %d", id),
                loggingAspect.currentUserId()
        );

        return consultationRepository.save(existingConsultation);
    }


    @Override
    @Transactional
    public Consultation findById(Long id) {
        return consultationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Consultation not found with ID: " + id));
    }


    @Override
    @Transactional
    public List<Consultation> findAll() {
        return consultationRepository.findAll();
    }


    @Override
    @Transactional
    public DossierMedical findDossierMedicalByConsultationId(Long id) {
        Consultation consultation = findById(id);
        // Ensure dossierMedical is not null before returning
        if (consultation.getDossierMedical() == null) {
            throw new IllegalStateException("Consultation with ID: " + id + " does not have an associated Dossier Medical.");
        }
        return consultation.getDossierMedical();
    }


    @Override
    @Transactional
    public RendezVous findRendezVousByConsultationId(Long id) {
        Consultation consultation = findById(id);
        // Ensure rendezVous is not null before returning
        if (consultation.getRendezVous() == null) {
            throw new IllegalStateException("Consultation with ID: " + id + " is not associated with a RendezVous.");
        }
        return consultation.getRendezVous();
    }


    @Override
    @Transactional
    public void deleteById(Long id) {
        Consultation consultation = findById(id);
        // Disassociate RendezVous before deleting Consultation to prevent orphan FK
        if (consultation.getRendezVous() != null) {
            RendezVous rendezVous = consultation.getRendezVous();
            rendezVous.setConsultation(null); // Unlink
            rendezVousRepository.save(rendezVous);
        }

        historiqueActionService.enregistrerAction(
                String.format("Suppression consultation ID: %d", id),
                loggingAspect.currentUserId()
        );

        consultationRepository.delete(consultation);
    }



    @Override
    @Transactional
    public Prescription addPrescriptionToConsultation(Long consultationId, Prescription prescription) {
        Consultation consultation = findById(consultationId);

        // Ensure patient and dossierMedical are linked to the prescription from the consultation
        if (consultation.getDossierMedical() == null || consultation.getDossierMedical().getPatient() == null) {
            throw new IllegalStateException("Cannot add prescription to a consultation without a linked patient/dossier medical.");
        }
        prescription.setConsultation(consultation);
        prescription.setMedecin(consultation.getMedecin()); // Doctor who made the consultation
        prescription.setPatient(consultation.getDossierMedical().getPatient());
        prescription.setDossierMedical(consultation.getDossierMedical());

        historiqueActionService.enregistrerAction(
                String.format("ajout prescription à la consultation avec l' ID: %d", consultationId),
                loggingAspect.currentUserId()
        );

        return prescriptionRepository.save(prescription);
    }


    @Override
    @Transactional
    public List<Prescription> findPrescriptionsByConsultationId(Long consultationId) {
        Consultation consultation = findById(consultationId);
        // Ensure lazy collection is initialized or fetched eagerly
        return consultation.getPrescriptions();
    }
}