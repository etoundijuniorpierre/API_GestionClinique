package com.example.GestionClinique.service.serviceImpl;


import com.example.GestionClinique.model.entity.*;
import com.example.GestionClinique.model.entity.enumElem.ModePaiement;
import com.example.GestionClinique.model.entity.enumElem.StatutSalle;
import com.example.GestionClinique.repository.*;
import com.example.GestionClinique.service.ConsultationService;
import com.example.GestionClinique.service.FactureService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
public class ConsultationServiceImpl implements ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final DossierMedicalRepository dossierMedicalRepository;
    private final UtilisateurRepository utilisateurRepository; // Assuming this is for Medecin
    private final RendezVousRepository rendezVousRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final SalleRepository salleRepository;
    private final FactureService factureService;

    @Autowired
    public ConsultationServiceImpl(
            ConsultationRepository consultationRepository,
            DossierMedicalRepository dossierMedicalRepository,
            UtilisateurRepository utilisateurRepository,
            RendezVousRepository rendezVousRepository,
            PrescriptionRepository prescriptionRepository, SalleRepository salleRepository, FactureService factureService) {
        this.consultationRepository = consultationRepository;
        this.dossierMedicalRepository = dossierMedicalRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.rendezVousRepository = rendezVousRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.salleRepository = salleRepository;
        this.factureService = factureService;
    }

    // This is for EMERGENCY consultations (no RendezVous)
    @Override
    @Transactional
    public Consultation createConsultation(Consultation consultation, Long medecinId) {

        Utilisateur medecin = utilisateurRepository.findById(medecinId)
                .orElseThrow(() -> new IllegalArgumentException("Medecin not found with ID: " + medecinId));
        consultation.setMedecin(medecin);

        // --- SCENARIO 2: Patient and Dossier Medical Null for now ---
        // If DossierMedical ID is provided in the DTO, try to link it.
        // Otherwise, explicitly set to null as requested for emergency cases.
        if (consultation.getDossierMedical() != null && consultation.getDossierMedical().getId() != null) {
            DossierMedical dossierMedical = dossierMedicalRepository.findById(consultation.getDossierMedical().getId())
                    .orElseThrow(() -> new IllegalArgumentException("DossierMedical not found with ID: " + consultation.getDossierMedical().getId()));
            consultation.setDossierMedical(dossierMedical);
        } else {
            // As per requirement, set to null if not provided for emergency
            consultation.setDossierMedical(null);
            // Ensure no RendezVous is linked for emergency consultations
            if (consultation.getRendezVous() != null) {
                throw new RuntimeException("Emergency consultation cannot be linked to a RendezVous.");
            }
        }

        // --- Handle Prescriptions (only if DossierMedical is present and has a patient) ---
        List<Prescription> savedPrescriptions = new ArrayList<>();
        if (consultation.getPrescriptions() != null && !consultation.getPrescriptions().isEmpty()) {
            if (consultation.getDossierMedical() != null && consultation.getDossierMedical().getPatient() != null) {
                for (Prescription prescription : consultation.getPrescriptions()) {
                    prescription.setConsultation(consultation); // Link prescription to this consultation
                    prescription.setMedecin(medecin);
                    prescription.setPatient(consultation.getDossierMedical().getPatient());
                    prescription.setDossierMedical(consultation.getDossierMedical());
                    savedPrescriptions.add(prescriptionRepository.save(prescription));
                }
                consultation.setPrescriptions(savedPrescriptions); // Update list with persisted entities
            } else {
                // Log a warning or throw if prescriptions are mandatory even for null patients
                System.out.println("Warning: Prescriptions provided for emergency consultation without a linked DossierMedical/Patient. Prescriptions will not be saved.");
                consultation.setPrescriptions(new ArrayList<>()); // Clear if not saving
            }
        }

        Consultation savedConsultation = consultationRepository.save(consultation);

        // --- Create Invoice (only if DossierMedical is present) ---
        if (savedConsultation.getDossierMedical() != null) {
            // Default ModePaiement for emergency, or get it from DTO if applicable
            factureService.generateInvoiceForConsultation(savedConsultation.getId(), ModePaiement.ESPECES);
        } else {
            System.out.println("No invoice generated for emergency consultation with no linked DossierMedical.");
        }

        return savedConsultation;
    }


    // This is for SCHEDULED consultations (linked to a RendezVous)
    @Override
    @Transactional
    public Consultation startConsultation(Long rendezVousId, Consultation consultationDetails, Long medecinId) {
        RendezVous rendezVous = rendezVousRepository.findById(rendezVousId)
                .orElseThrow(() -> new IllegalArgumentException("RendezVous not found with ID: " + rendezVousId));

        if (rendezVous.getConsultation() != null) {
            throw new RuntimeException("RendezVous with ID " + rendezVousId + " is already linked to a consultation.");
        }

        // --- SCENARIO 1: Physician is the logged-in user ---
        // The medecinId parameter should already come from the authenticated user.
        Utilisateur medecin = utilisateurRepository.findById(medecinId)
                .orElseThrow(() -> new IllegalArgumentException("Medecin not found with ID: " + medecinId));
        consultationDetails.setMedecin(medecin);

        // --- SCENARIO 1: Mark the room as OCCUPEE ---
        Salle salle = rendezVous.getSalle();
        if (salle != null) {
            salle.setStatutSalle(StatutSalle.OCCUPEE);
            salleRepository.save(salle); // Save the updated room status
        } else {
            throw new IllegalStateException("RendezVous does not have an associated room to mark as occupied.");
        }


        consultationDetails.setRendezVous(rendezVous);

        // Inherit DossierMedical from the Patient associated with the RendezVous
        if (rendezVous.getPatient() != null && rendezVous.getPatient().getDossierMedical() != null) {
            consultationDetails.setDossierMedical(rendezVous.getPatient().getDossierMedical());
        } else {
            throw new RuntimeException("RendezVous patient does not have an associated medical record.");
        }

        // --- Handle Prescriptions ---
        List<Prescription> savedPrescriptions = new ArrayList<>();
        if (consultationDetails.getPrescriptions() != null && !consultationDetails.getPrescriptions().isEmpty()) {
            for (Prescription prescription : consultationDetails.getPrescriptions()) {
                prescription.setConsultation(consultationDetails); // Link prescription to this consultation
                prescription.setMedecin(medecin); // Associate with the current doctor
                prescription.setPatient(rendezVous.getPatient()); // Link to patient from RendezVous
                prescription.setDossierMedical(rendezVous.getPatient().getDossierMedical()); // Link to dossier from RendezVous
                savedPrescriptions.add(prescriptionRepository.save(prescription));
            }
            consultationDetails.setPrescriptions(savedPrescriptions);
        }

        Consultation newConsultation = consultationRepository.save(consultationDetails);

        // Update RendezVous to link to this new Consultation (to maintain bi-directional integrity)
        rendezVous.setConsultation(newConsultation);
        rendezVousRepository.save(rendezVous);

        // --- SCENARIO 1: Create the Invoice ---
        // Default ModePaiement for scheduled, or get it from DTO if applicable
        factureService.generateInvoiceForConsultation(newConsultation.getId(), ModePaiement.ESPECES);

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

        // Handle prescriptions updates if necessary (this method currently just updates basic fields)
        // You'd need a more complex logic here to add/remove/update prescriptions.
        // For simplicity, let's assume prescriptions are added/updated via addPrescriptionToConsultation for now.

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