package com.example.GestionClinique.service.serviceImpl;


import com.example.GestionClinique.dto.InfoPersonnelDto;
import com.example.GestionClinique.dto.PatientDto;
import com.example.GestionClinique.model.entity.InfoPersonnel;
import com.example.GestionClinique.model.entity.Patient;
import com.example.GestionClinique.repository.PatientRepository;
import com.example.GestionClinique.service.HistoriqueActionService; // Import HistoriqueActionService
import com.example.GestionClinique.service.PatientService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final HistoriqueActionService historiqueActionService; // Inject HistoriqueActionService

    @Autowired
    public PatientServiceImpl(PatientRepository patientRepository, HistoriqueActionService historiqueActionService) { // Add to constructor
        this.patientRepository = patientRepository;
        this.historiqueActionService = historiqueActionService; // Initialize
    }



    @Override
    @Transactional
    public PatientDto createPatient(PatientDto patientDto) {

        if (patientDto.getInfoPersonnel() == null || patientDto.getInfoPersonnel().getEmail() == null || patientDto.getInfoPersonnel().getNom() == null || patientDto.getInfoPersonnel().getPrenom() == null) {
            throw new IllegalArgumentException("Les informations personnelles (nom, prenom, email) du patient sont obligatoires.");
        }

        if (patientRepository.findPatientByInfoPersonnel_Email(patientDto.getInfoPersonnel().getEmail()).isPresent()) {
            throw new IllegalArgumentException("Un patient avec l'email '" + patientDto.getInfoPersonnel().getEmail() + "' existe déjà.");
        }

        Patient patientToSave = PatientDto.toEntity(patientDto);
        if (patientToSave.getInfoPersonnel() == null) {
            patientToSave.setInfoPersonnel(new InfoPersonnel());
        }

        PatientDto savedPatient = PatientDto.fromEntity(
                patientRepository.save(
                        patientToSave
                )
        );

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Création du patient ID: " + savedPatient.getId() + ", Nom: " + savedPatient.getInfoPersonnel().getNom() + " " + savedPatient.getInfoPersonnel().getPrenom()
        );
        // --- Fin de l'ajout de l'historique ---

        return savedPatient;
    }



    @Override
    @Transactional
    public PatientDto updatePatient(Integer id, PatientDto patientDto) {
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("le patient avec l'ID " + id + " n'existe pas"));

        if (existingPatient.getInfoPersonnel() == null) {
            existingPatient.setInfoPersonnel(new InfoPersonnel());
        }

        // Update personal information if provided in the DTO
        if (patientDto.getInfoPersonnel() != null) {
            InfoPersonnelDto dtoInfo = patientDto.getInfoPersonnel();

            if (dtoInfo.getNom() != null && !dtoInfo.getNom().trim().isEmpty()) {
                existingPatient.getInfoPersonnel().setNom(dtoInfo.getNom());
            }
            if (dtoInfo.getPrenom() != null && !dtoInfo.getPrenom().trim().isEmpty()) {
                existingPatient.getInfoPersonnel().setPrenom(dtoInfo.getPrenom());
            }
            // Email update requires special handling: check for uniqueness
            if (dtoInfo.getEmail() != null && !dtoInfo.getEmail().trim().isEmpty()) {
                // Only allow email update if it's different from the current one AND not already used by another patient
                if (!dtoInfo.getEmail().equals(existingPatient.getInfoPersonnel().getEmail())) {
                    if (patientRepository.findPatientByInfoPersonnel_Email(dtoInfo.getEmail()).isPresent()) {
                        throw new IllegalArgumentException("L'email '" + dtoInfo.getEmail() + "' est déjà utilisé par un autre patient.");
                    }
                }
                existingPatient.getInfoPersonnel().setEmail(dtoInfo.getEmail());
            }
            if (dtoInfo.getDateNaissance() != null) {
                existingPatient.getInfoPersonnel().setDateNaissance(dtoInfo.getDateNaissance());
            }
            if (dtoInfo.getTelephone() != null && !dtoInfo.getTelephone().trim().isEmpty()) {
                existingPatient.getInfoPersonnel().setTelephone(dtoInfo.getTelephone());
            }
            if (dtoInfo.getAdresse() != null && !dtoInfo.getAdresse().trim().isEmpty()) {
                existingPatient.getInfoPersonnel().setAdresse(dtoInfo.getAdresse());
            }
            if (dtoInfo.getGenre() != null) { // Assuming Genre is an Enum or String
                existingPatient.getInfoPersonnel().setGenre(dtoInfo.getGenre());
            }
        }

        PatientDto updatedPatient = PatientDto.fromEntity(
                patientRepository.save(
                        existingPatient
                )
        );

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Mise à jour du patient ID: " + id + ", Nom: " + updatedPatient.getInfoPersonnel().getNom() + " " + updatedPatient.getInfoPersonnel().getPrenom()
        );
        // --- Fin de l'ajout de l'historique ---

        return updatedPatient;
    }



    @Override
    @Transactional
    public List<PatientDto> findAllPatients() {
        List<PatientDto> allPatients = patientRepository.findAll()
                .stream()
                .map(PatientDto::fromEntity)
                .collect(Collectors.toList());

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Affichage de tous les patients."
        );
        // --- Fin de l'ajout de l'historique ---

        return allPatients;
    }



    @Override
    @Transactional
    public PatientDto findById(Integer id) {
        PatientDto foundPatient = patientRepository.findById(id)
                .map(PatientDto::fromEntity)
                .orElseThrow(() -> new RuntimeException("le patient n'existe pas"));

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Recherche du patient ID: " + id + ", Nom: " + foundPatient.getInfoPersonnel().getNom() + " " + foundPatient.getInfoPersonnel().getPrenom()
        );
        // --- Fin de l'ajout de l'historique ---

        return foundPatient;
    }



    @Override
    @Transactional
    public void deletePatient(Integer id) {
        if (!patientRepository.existsById(id)) {
            throw new RuntimeException("le patient n'existe pas");
        }

        patientRepository.deleteById(id);

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Suppression du patient ID: " + id
        );
        // --- Fin de l'ajout de l'historique ---
    }



    @Override
    @Transactional
    public List<PatientDto> searchPatients(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            List<PatientDto> allPatients = findAllPatients();
            historiqueActionService.enregistrerAction(
                    "Recherche de tous les patients (terme de recherche vide)."
            );
            return allPatients;
        }
        List<PatientDto> searchedPatients = patientRepository.findPatientBySearchTerm(searchTerm)
                .stream()
                .filter(Objects::nonNull)
                .map(PatientDto::fromEntity)
                .collect(Collectors.toList());

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Recherche de patients avec le terme: '" + searchTerm + "' (nombre de résultats: " + searchedPatients.size() + ")"
        );
        // --- Fin de l'ajout de l'historique ---

        return searchedPatients;
    }



    @Override
    @Transactional
    public List<PatientDto> findPatientByInfoPersonnel_Nom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide pour la recherche.");
        }
        List<PatientDto> patientsByNom = patientRepository.findPatientByInfoPersonnel_Nom(nom)
                .stream()
                .filter(Objects::nonNull)
                .map(PatientDto::fromEntity)
                .collect(Collectors.toList());

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Recherche de patients par nom: '" + nom + "' (nombre de résultats: " + patientsByNom.size() + ")"
        );
        // --- Fin de l'ajout de l'historique ---

        return patientsByNom;
    }



    @Override
    @Transactional
    public PatientDto findPatientByInfoPersonnel_Email(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'email ne peut pas être vide pour la recherche.");
        }
        PatientDto patientByEmail = patientRepository.findPatientByInfoPersonnel_Email(email)
                .map(PatientDto::fromEntity)
                .orElseThrow(() -> new RuntimeException("le patient avec l'email " + email + " n'existe pas"));

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Recherche du patient par email: '" + email + "'"
        );
        // --- Fin de l'ajout de l'historique ---

        return patientByEmail;
    }
}