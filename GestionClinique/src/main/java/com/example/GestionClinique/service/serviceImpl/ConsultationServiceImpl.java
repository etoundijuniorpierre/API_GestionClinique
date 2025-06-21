package com.example.GestionClinique.service.serviceImpl;

import com.example.GestionClinique.dto.*;
import com.example.GestionClinique.model.entity.*;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import com.example.GestionClinique.repository.*;
import com.example.GestionClinique.service.ConsultationService;
import com.example.GestionClinique.service.HistoriqueActionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ConsultationServiceImpl implements ConsultationService {
    private final ConsultationRepository consultationRepository;
    private final RendezVousRepository rendezVousRepository;
    private final DossierMedicalRepository dossierMedicalRepository;
    private final PrescriptionRepository prescriptionRepository; // Peut être nécessaire si les prescriptions sont gérées indépendamment
    private final HistoriqueActionRepository historiqueActionRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final HistoriqueActionService historiqueActionService;


    @Autowired
    public ConsultationServiceImpl(ConsultationRepository consultationRepository,
                                   RendezVousRepository rendezVousRepository,
                                   DossierMedicalRepository dossierMedicalRepository,
                                   PrescriptionRepository prescriptionRepository,
                                   HistoriqueActionRepository historiqueActionRepository, UtilisateurRepository utilisateurRepository, HistoriqueActionService historiqueActionService) {
        this.consultationRepository = consultationRepository;
        this.rendezVousRepository = rendezVousRepository;
        this.dossierMedicalRepository = dossierMedicalRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.historiqueActionRepository = historiqueActionRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.historiqueActionService = historiqueActionService;
    }


    @Override
    @Transactional
    public ConsultationDto createConsultation(ConsultationDto consultationDto) {
        // Valider si les IDs des entités liées existent (RendezVous, DossierMedical) avant de sauvegarder
        // C'est une bonne pratique pour éviter les erreurs de clé étrangère
        if (consultationDto.getRendezVous() != null && consultationDto.getRendezVous().getId() != null) {
            rendezVousRepository.findById(consultationDto.getRendezVous().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Rendez-vous associé introuvable"));
        }
        if (consultationDto.getDossierMedical() != null && consultationDto.getDossierMedical().getId() != null) {
            dossierMedicalRepository.findById(consultationDto.getDossierMedical().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Dossier médical associé introuvable"));
        }

        ConsultationDto savedConsultationDto = ConsultationDto.fromEntity(
                consultationRepository.save(
                        ConsultationDto.toEntity(consultationDto)
                )
        );
        
        // Utilisez le service d'historique
        historiqueActionService.enregistrerAction(
                "Création d'une consultation (ID consultation: " + savedConsultationDto.getId() + ", ID Rendez-vous: " + consultationDto.getRendezVous().getId() + ")"
        );


        return savedConsultationDto;
    }




    @Override
    @Transactional
    public ConsultationDto updateConsultation(Integer id, ConsultationDto consultationDto) {
        // 1. Récupérer l'entité existante
        Consultation existingConsultation = consultationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("La consultation avec l'ID " + id + " n'existe pas."));

        // 2. Mettre à jour les champs de l'entité existante avec les données du DTO
        // Cela est crucial pour ne pas écraser les relations ou d'autres champs non présents dans le DTO de mise à jour
        existingConsultation.setMotifs(consultationDto.getMotifs());
        existingConsultation.setTensionArterielle(consultationDto.getTensionArterielle());
        existingConsultation.setTemperature(consultationDto.getTemperature());
        existingConsultation.setPoids(consultationDto.getPoids());
        existingConsultation.setTaille(consultationDto.getTaille());
        existingConsultation.setCompteRendu(consultationDto.getCompteRendu());
        existingConsultation.setDiagnostic(consultationDto.getDiagnostic());

        // Gérer la mise à jour des relations si le DTO les contient et si elles sont censées être modifiables
        // Par exemple, si le dossier médical ou le rendez-vous peut changer (ce qui est rare pour une consultation)
        if (consultationDto.getDossierMedical() != null && consultationDto.getDossierMedical().getId() != null) {
            DossierMedical dossierMedical = dossierMedicalRepository.findById(consultationDto.getDossierMedical().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Dossier médical associé introuvable"));
            existingConsultation.setDossierMedical(dossierMedical);
        }
        // Le rendez-vous est généralement OneToOne et ne change pas après création

        // Note: La gestion des prescriptions est traitée par addPrescriptionToConsultation

        // Enregistrer l'action via le service d'historique
        historiqueActionService.enregistrerAction(
                "Mise à jour de la consultation ID: " + id
        );

        // 3. Sauvegarder l'entité mise à jour
        return ConsultationDto.fromEntity(consultationRepository.save(existingConsultation));
    }


    @Override
    @Transactional
    public ConsultationDto findById(Integer id) {

        // Enregistrer l'action via le service d'historique
        historiqueActionService.enregistrerAction(
                "Consultation ID: " + id + " recherchée"
        );

        return consultationRepository.findById(id)
                .map(ConsultationDto::fromEntity)
                .orElseThrow(() ->
                        new RuntimeException("La consultation avec l'ID " + id + " n'existe pas."));
    }

    @Override
    @Transactional
    public List<ConsultationDto> findAll() {

        // Enregistrer l'action via le service d'historique
        historiqueActionService.enregistrerAction(
                "Liste de toutes les consultations affichée"
        );

        return consultationRepository.findAll().stream()
                .map(ConsultationDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DossierMedicalDto findDossierMedicalByConsultationId(Integer id) {

        // Enregistrer l'action via le service d'historique
        historiqueActionService.enregistrerAction(
                "Dossier médical lié à la consultation ID: " + id + " affiché"
        );

        return consultationRepository.findById(id)
                .map(Consultation::getDossierMedical)
                .map(DossierMedicalDto::fromEntity)
                .orElseThrow(() ->
                        new RuntimeException("Consultation introuvable ou dossier médical non associé"));
    }

    @Override
    @Transactional
    public RendezVousDto findRendezVousByConsultationId(Integer id) {

        // Enregistrer l'action via le service d'historique
        historiqueActionService.enregistrerAction(
                "Rendez-vous lié à la consultation ID: " + id + " affiché"
        );

        return consultationRepository.findById(id)
                .map(Consultation::getRendezVous)
                .map(RendezVousDto::fromEntity)
                .orElseThrow(() ->
                        new RuntimeException("Consultation introuvable ou rendez-vous non associé"));
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {

        Consultation consultationToDelete = consultationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("La consultation avec l'ID " + id + " n'existe pas et ne peut pas être supprimée."));

        if (consultationToDelete.getRendezVous() != null) {
            RendezVous rendezVousAssocie = consultationToDelete.getRendezVous();
            rendezVousAssocie.setConsultation(null); // Délier la consultation du rendez-vous
            rendezVousRepository.save(rendezVousAssocie); // Sauvegarder le rendez-vous mis à jour
        }

        consultationRepository.deleteById(id);


        // Enregistrer l'action via le service d'historique
        historiqueActionService.enregistrerAction(
                "Suppression de la consultation ID: " + id
        );
    }

    @Override
    @Transactional
    public ConsultationDto startConsultation(Integer rendezVousId, ConsultationDto consultationDetails) {
        // 1. Récupérer le RendezVous par rendezVousId
        RendezVous rendezVous = rendezVousRepository.findById(rendezVousId)
                .orElseThrow(() -> new RuntimeException("Rendez-vous introuvable avec l'ID " + rendezVousId));

        // 2. Valider le statut du RendezVous
        if (rendezVous.getStatut() != StatutRDV.CONFIRME) {
            throw new RuntimeException("Impossible de démarrer la consultation: le rendez-vous n'est pas CONFIRME.");
        }

        // Vérifier si le rendez-vous n'est pas déjà lié à une consultation
        if (rendezVous.getConsultation() != null) {
            throw new IllegalStateException("Le rendez-vous avec l'ID " + rendezVousId + " est déjà lié à une consultation.");
        }

        // 3. S'assurer que le patient du rendez-vous a un dossier médical. Si non, créer un dossier médical initial.
        // C'est une décision de conception : soit on force la création ici, soit on assume qu'il est déjà créé.
        // Ici, je vais vérifier et créer si nécessaire.
        DossierMedical dossierMedical = Optional.ofNullable(rendezVous.getPatient().getDossierMedical())
                .orElseGet(() -> {
                    DossierMedical newDossier = new DossierMedical();
                    newDossier.setPatient(rendezVous.getPatient()); // Lier le dossier au patient
                    // Initialiser d'autres champs de dossier médical à vide ou par défaut
                    newDossier.setAntecedents("");
                    newDossier.setAllergies("");
                    newDossier.setTraitementsEnCours("");
                    newDossier.setObservations("");
                    return dossierMedicalRepository.save(newDossier);
                });

        // 4. Créer l'entité Consultation à partir du DTO
        Consultation newConsultation = ConsultationDto.toEntity(consultationDetails);
        newConsultation.setRendezVous(rendezVous); // Lier la consultation au rendez-vous
        newConsultation.setDossierMedical(dossierMedical); // Lier la consultation au dossier médical du patient
        newConsultation.setMedecin(rendezVous.getMedecin()); // Lier le médecin du rendez-vous à la consultation
        // 5. Sauvegarder la nouvelle consultation
        Consultation savedConsultation = consultationRepository.save(newConsultation);

        // 6. Mettre à jour le statut du RendezVous à 'TERMINE'
        rendezVous.setStatut(StatutRDV.TERMINE);
        rendezVous.setConsultation(savedConsultation);
        rendezVousRepository.save(rendezVous); // Sauvegarder le rendez-vous mis à jour

        // Enregistrer l'action via le service d'historique
        historiqueActionService.enregistrerAction(
                "Lancement de la consultation ID: " + savedConsultation.getId() + " pour le rendez-vous ID: " + rendezVousId
        );

        return ConsultationDto.fromEntity(savedConsultation);

    }

    @Override
    @Transactional
    public ConsultationDto addPrescriptionToConsultation(Integer consultationId, PrescriptionDto prescriptionDto) {
        // 1. Trouver la Consultation par consultationId
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new RuntimeException("Consultation introuvable avec l'ID " + consultationId));

        // 2. Créer l'entité Prescription à partir du DTO
        Prescription newPrescription = PrescriptionDto.toEntity(prescriptionDto);

        // 3. Lier la nouvelle prescription à la consultation
        // Assurez-vous que l'entité Prescription a une relation @ManyToOne avec Consultation
        newPrescription.setConsultation(consultation);

        // Assurez-vous aussi que le patient et le médecin sont liés à la prescription
        // Ces informations devraient idéalement venir du DTO de prescription ou de la consultation
        newPrescription.setPatient(consultation.getDossierMedical().getPatient());
        newPrescription.setMedecin(consultation.getRendezVous().getMedecin()); // Le médecin du RDV

        if (newPrescription.getDatePrescription() == null) {
            newPrescription.setDatePrescription(LocalDate.now());
        }

        // Si la relation entre Consultation et Prescription est bidirectionnelle (OneToMany),
        // il faut aussi ajouter la prescription à la liste des prescriptions de la consultation.
        // Assurez-vous que `getPrescriptions()` retourne une liste modifiable.
        if (consultation.getPrescriptions() == null) {
            consultation.setPrescriptions(new java.util.ArrayList<>());
        }
        consultation.getPrescriptions().add(newPrescription);

        // Enregistrer l'action via le service d'historique
        historiqueActionService.enregistrerAction(
                "Ajout d'une prescription à la consultation ID: " + consultationId
        );

        // 4. Sauvegarder la consultation mise à jour (ceci peut aussi cascader la sauvegarde de la prescription si configuré)
        // Alternativement, si PrescriptionRepository est injecté, on peut faire:
        // prescriptionRepository.save(newPrescription);
        // et ensuite juste sauver la consultation pour s'assurer que la liste est à jour.
        return ConsultationDto.fromEntity(consultationRepository.save(consultation));
    }

    @Override
    @Transactional
    public List<PrescriptionDto> findPrescriptionsByConsultationId(Integer consultationId) {
        // Logique pour récupérer toutes les prescriptions d'une consultation donnée
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new RuntimeException("Consultation introuvable avec l'ID " + consultationId));

        // Assurez-vous que la liste des prescriptions est chargée (potential problem with LAZY loading)
        // Vous pouvez utiliser @EntityGraph ou charger explicitement si nécessaire.
        if (consultation.getPrescriptions() == null) {
            return List.of(); // Ou lancer une exception si la liste ne devrait jamais être nulle
        }

        // Enregistrer l'action via le service d'historique
        historiqueActionService.enregistrerAction(
                "Affichage des prescriptions pour la consultation ID: " + consultationId
        );

        return consultation.getPrescriptions().stream()
                .filter(Objects::nonNull) // Filtre les prescriptions nulles, au cas où
                .map(PrescriptionDto::fromEntity)
                .collect(Collectors.toList());
    }

}
