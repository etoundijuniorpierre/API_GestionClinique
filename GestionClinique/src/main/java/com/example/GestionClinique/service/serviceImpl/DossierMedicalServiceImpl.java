package com.example.GestionClinique.service.serviceImpl;

import com.example.GestionClinique.dto.DossierMedicalDto;
import com.example.GestionClinique.dto.PatientDto;
import com.example.GestionClinique.model.entity.DossierMedical;
import com.example.GestionClinique.model.entity.Patient;
import com.example.GestionClinique.repository.DossierMedicalRepository;
import com.example.GestionClinique.repository.PatientRepository;
import com.example.GestionClinique.service.DossierMedicalService;
import com.example.GestionClinique.service.HistoriqueActionService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DossierMedicalServiceImpl implements DossierMedicalService {
    private final DossierMedicalRepository dossierMedicalRepository;
    private final PatientRepository patientRepository;
    private final HistoriqueActionService historiqueActionService; // Inject historiqueActionService

    @Autowired
    public DossierMedicalServiceImpl(DossierMedicalRepository dossierMedicalRepository,
                                     PatientRepository patientRepository,
                                     HistoriqueActionService historiqueActionService) { // Add to constructor
        this.dossierMedicalRepository = dossierMedicalRepository;
        this.patientRepository = patientRepository;
        this.historiqueActionService = historiqueActionService; // Initialize
    }

//    @Override
//    @Transactional
//    public DossierMedicalDto createDossierMedical(DossierMedicalDto dossierMedicalDto) {
//        // Logique pour créer un nouveau dossier médical.
//        // Cette méthode est utilisée quand le patient est déjà connu par son DTO,
//        // mais on doit s'assurer qu'il n'a pas déjà un dossier.
//
//        // 1. Vérifier si un patient est fourni dans le DTO du dossier médical
//        if (dossierMedicalDto.getPatient() == null || dossierMedicalDto.getPatient().getId() == null) {
//            throw new IllegalArgumentException("Un patient doit être associé pour créer un dossier médical.");
//        }
//
//        Integer patientId = dossierMedicalDto.getPatient().getId();
//        Patient patient = patientRepository.findById(patientId)
//                .orElseThrow(() -> new RuntimeException("Patient introuvable avec l'ID " + patientId));
//
//        // 2. Vérifier si le patient a déjà un dossier médical
//        if (patient.getDossierMedical() != null) {
//            throw new IllegalStateException("Le patient avec l'ID " + patientId + " possède déjà un dossier médical.");
//        }
//
//        // 3. Mapper le DTO vers l'entité et lier le patient
//        DossierMedical newDossierMedical = DossierMedicalDto.toEntity(dossierMedicalDto);
//        newDossierMedical.setPatient(patient); // Lier l'entité Patient au DossierMedical
//
//        // 4. Sauvegarder le nouveau dossier médical
//        return DossierMedicalDto.fromEntity(dossierMedicalRepository.save(newDossierMedical));
//    }

    @Override
    @Transactional
    public DossierMedicalDto createDossierMedicalForPatient(Integer patientId, DossierMedicalDto dossierMedicalDto) {
        // Logique pour créer un dossier médical et le lier à un patient spécifique
        // 1. Trouver le Patient par patientId
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient introuvable avec l'ID " + patientId));

        // 2.vérifier que la patient à ou non un dossier médical
        if (patient.getDossierMedical() != null && patient.getDossierMedical().getId() != null) {
            throw new IllegalStateException("Le patient avec l'ID " + patientId + " possède déjà un dossier médical.");
        }

        // 3. Créer le DossierMedical et le lier au Patient
        DossierMedical newDossierMedical = DossierMedicalDto.toEntity(dossierMedicalDto);
        newDossierMedical.setPatient(patient);

        DossierMedicalDto savedDossier = DossierMedicalDto.fromEntity(dossierMedicalRepository.save(newDossierMedical));

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Création du dossier médical ID: " + savedDossier.getId() + " pour le patient ID: " + patientId
        );
        // --- Fin de l'ajout de l'historique ---

        return savedDossier;
    }



    @Override
    @Transactional
    public DossierMedicalDto updateDossierMedical(Integer id, DossierMedicalDto dossierMedicalDto) {
        DossierMedical existingDossierMedical = dossierMedicalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dossier medical introuvable avec l'ID " + id));

        existingDossierMedical.setAntecedents(dossierMedicalDto.getAntecedents());
        existingDossierMedical.setTraitementsEnCours(dossierMedicalDto.getTraitementsEnCours());
        existingDossierMedical.setAllergies(dossierMedicalDto.getAllergies());
        existingDossierMedical.setObservations(dossierMedicalDto.getObservations());

        DossierMedicalDto updatedDossier = DossierMedicalDto.fromEntity(
                dossierMedicalRepository.save(existingDossierMedical)
        );

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Mise à jour du dossier médical ID: " + id + " pour le patient ID: " + existingDossierMedical.getPatient().getId()
        );
        // --- Fin de l'ajout de l'historique ---

        return updatedDossier;
    }



    @Override
    @Transactional
    public DossierMedicalDto findDossierMedicalById(Integer id) {
        DossierMedicalDto foundDossier = dossierMedicalRepository.findById(id)
                .map(DossierMedicalDto::fromEntity)
                .orElseThrow(() ->
                        new RuntimeException("Le Dossier medical n'existe pas"));

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Recherche du dossier médical ID: " + id
        );
        // --- Fin de l'ajout de l'historique ---

        return foundDossier;
    }



    @Override
    @Transactional
    public List<DossierMedicalDto> findAllDossierMedical() {
        List<DossierMedicalDto> allDossiers = dossierMedicalRepository.findAll()
                .stream()
                .map(DossierMedicalDto::fromEntity)
                .collect(Collectors.toList());

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Affichage de tous les dossiers médicaux"
        );
        // --- Fin de l'ajout de l'historique ---

        return allDossiers;
    }



    @Override
    @Transactional
    public PatientDto findPatientByDossierMedicalId(Integer dossierMedicalId) {
        DossierMedical dossierMedical = dossierMedicalRepository.findById(dossierMedicalId)
                .orElseThrow(() -> new RuntimeException("Dossier médical introuvable avec l'ID " + dossierMedicalId));

        PatientDto patientDto = Optional.ofNullable(dossierMedical.getPatient())
                .map(PatientDto::fromEntity)
                .orElseThrow(() -> new RuntimeException("Aucun patient associé au dossier médical avec l'ID " + dossierMedicalId));

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Recherche du patient associé au dossier médical ID: " + dossierMedicalId
        );
        // --- Fin de l'ajout de l'historique ---

        return patientDto;
    }



    @Override
    @Transactional
    public void deleteDossierMedicalById(Integer id) {
        //vérifions si le doc medical existe
        if(!dossierMedicalRepository.existsById(id)) {
            throw new RuntimeException("Le Dossier medical avec l'ID : "+id+" n'existe pas");
        }
        dossierMedicalRepository.deleteById(id);

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Suppression du dossier médical ID: " + id
        );
        // --- Fin de l'ajout de l'historique ---
    }



    @Override
    @Transactional
    public DossierMedicalDto findDossierMedicalByPatientId(Integer patientId) {
        //trouver d'abord le patient
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient introuvable"));

        //accèder au dossier medical tout en gérant le cas où celui-ci n'en n'aurait pas encore
        DossierMedicalDto dossierMedicalDto = Optional.ofNullable(patient.getDossierMedical())
                .map(DossierMedicalDto::fromEntity)
                .orElseThrow(() -> new RuntimeException("Dossier médical introuvable pour le patient ID: " + patientId));

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Recherche du dossier médical pour le patient ID: " + patientId
        );
        // --- Fin de l'ajout de l'historique ---

        return dossierMedicalDto;
    }
}

