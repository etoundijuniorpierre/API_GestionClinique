package com.example.GestionClinique.service.serviceImpl;

import com.example.GestionClinique.dto.RequestDto.ConsultationRequestDto;
import com.example.GestionClinique.dto.RequestDto.DossierMedicalRequestDto;
import com.example.GestionClinique.dto.RequestDto.PrescriptionRequestDto;
import com.example.GestionClinique.dto.RequestDto.RendezVousRequestDto;
import com.example.GestionClinique.model.entity.*;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import com.example.GestionClinique.repository.*;
import com.example.GestionClinique.service.ConsultationService;
import com.example.GestionClinique.service.HistoriqueActionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ConsultationServiceImpl implements ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final DossierMedicalRepository dossierMedicalRepository;
    private final UtilisateurRepository utilisateurRepository; // Assuming this is for Medecin
    private final RendezVousRepository rendezVousRepository;
    private final PrescriptionRepository prescriptionRepository;

    @Autowired
    public ConsultationServiceImpl(
            ConsultationRepository consultationRepository,
            DossierMedicalRepository dossierMedicalRepository,
            UtilisateurRepository utilisateurRepository,
            RendezVousRepository rendezVousRepository,
            PrescriptionRepository prescriptionRepository) {
        this.consultationRepository = consultationRepository;
        this.dossierMedicalRepository = dossierMedicalRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.rendezVousRepository = rendezVousRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    // This is for EMERGENCY consultations (no RendezVous)
    @Override
    public Consultation createConsultation(Consultation consultation, Long medecinId) {
        
        Utilisateur medecin = utilisateurRepository.findById(medecinId)
                .orElseThrow(() -> new IllegalArgumentException("Medecin not found with ID: " + medecinId));
        consultation.setMedecin(medecin);

        // Fetch DossierMedical
        DossierMedical dossierMedical = dossierMedicalRepository.findById(consultation.getDossierMedical().getId())
                .orElseThrow(() -> new IllegalArgumentException("DossierMedical not found with ID: " + consultation.getDossierMedical().getId()));
        consultation.setDossierMedical(dossierMedical);
        
        if (consultation.getRendezVous() != null) {
            throw new RuntimeException("Emergency consultation cannot be linked to a RendezVous.");
        }
        
        if (consultation.getDateHeureDebut() == null) {
            consultation.setDateHeureDebut(LocalDateTime.now());
        }
        if (consultation.getDureeMinutes() == null || consultation.getDureeMinutes() <= 0) {
            consultation.setDureeMinutes(30L); 
        }


        return consultationRepository.save(consultation);
    }


    // This is for SCHEDULED consultations (linked to a RendezVous)
    @Override
    public Consultation startConsultation(Long rendezVousId, Consultation consultationDetails, Long medecinId) {
        

        RendezVous rendezVous = rendezVousRepository.findById(rendezVousId)
                .orElseThrow(() -> new IllegalArgumentException("RendezVous not found with ID: " + rendezVousId));

        if (rendezVous.getConsultation() != null) {
            throw new RuntimeException("RendezVous with ID " + rendezVousId + " is already linked to a consultation.");
        }

        // Set the doctor who initiated the action (should match the one who created the rendez-vous normally)
        Utilisateur medecin = utilisateurRepository.findById(medecinId)
                .orElseThrow(() -> new IllegalArgumentException("Medecin not found with ID: " + medecinId));
        consultationDetails.setMedecin(medecin);


        // Link Consultation to RendezVous
        consultationDetails.setRendezVous(rendezVous);

        // Inherit DossierMedical from the Patient associated with the RendezVous
        if (rendezVous.getPatient() != null && rendezVous.getPatient().getDossierMedical() != null) {
            consultationDetails.setDossierMedical(rendezVous.getPatient().getDossierMedical());
        } else {
            throw new RuntimeException("RendezVous patient does not have an associated medical record.");
        }

        // Inherit date and duration from the RendezVous if they are not explicitly provided in consultationDetails
        // If your RendezVous entity also has dateHeureDebut/dureeMinutes for the slot
        // or if the DTO explicitly passes them.
        // For now, assuming dateHeureDebut and dureeMinutes are passed in consultationDetails DTO.
        if (consultationDetails.getDateHeureDebut() == null) {
            throw new RuntimeException("Date and time of start are required for scheduled consultation.");
        }
        if (consultationDetails.getDureeMinutes() == null || consultationDetails.getDureeMinutes() <= 0) {
            throw new RuntimeException("Duration of consultation is required.");
        }


        Consultation newConsultation = consultationRepository.save(consultationDetails);

        // Update RendezVous to link to this new Consultation (to maintain bi-directional integrity)
        rendezVous.setConsultation(newConsultation);
        rendezVousRepository.save(rendezVous);

        return newConsultation;
    }

    @Override
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
        existingConsultation.setDateHeureDebut(consultationDetails.getDateHeureDebut());
        existingConsultation.setDureeMinutes(consultationDetails.getDureeMinutes());

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
        return consultation.getDossierMedical();
    }

    @Override
    @Transactional
    public RendezVous findRendezVousByConsultationId(Long id) {
        Consultation consultation = findById(id);
        return consultation.getRendezVous();
    }

    @Override
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
    public Prescription addPrescriptionToConsultation(Long consultationId, Prescription prescription) {
        Consultation consultation = findById(consultationId);
        prescription.setConsultation(consultation);
        // Ensure that patient, medication etc. are set on the prescription if needed
        return prescriptionRepository.save(prescription);
    }

    @Override
    @Transactional
    public List<Prescription> findPrescriptionsByConsultationId(Long consultationId) {
        Consultation consultation = findById(consultationId);
        // It's often better to explicitly fetch in the repository for lazy collections
        // return prescriptionRepository.findByConsultationId(consultationId); // Assumes you add this method
        return consultation.getPrescriptions(); // Ensure lazy collection is initialized or fetched eagerly
    }
}