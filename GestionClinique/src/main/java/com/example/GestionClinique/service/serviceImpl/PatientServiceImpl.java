package com.example.GestionClinique.service.serviceImpl;

import com.example.GestionClinique.dto.RequestDto.InfoPersonnelRequestDto; // Ensure this is imported if InfoPersonnel is a separate DTO
import com.example.GestionClinique.dto.RequestDto.PatientRequestDto;
import com.example.GestionClinique.model.InfoPersonnel; // Ensure this is imported
import com.example.GestionClinique.model.entity.Patient;
import com.example.GestionClinique.repository.PatientRepository;
import com.example.GestionClinique.service.HistoriqueActionService;
import com.example.GestionClinique.service.PatientService;
import jakarta.persistence.EntityNotFoundException; // Use more specific exception
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final HistoriqueActionService historiqueActionService;

    @Autowired
    public PatientServiceImpl(PatientRepository patientRepository, HistoriqueActionService historiqueActionService) {
        this.patientRepository = patientRepository;
        this.historiqueActionService = historiqueActionService;
    }



    @Override
    @Transactional
    public PatientRequestDto createPatient(PatientRequestDto patientRequestDto) {
        // Validate required personal information
        if (patientRequestDto.getInfoPersonnel() == null ||
                patientRequestDto.getInfoPersonnel().getEmail() == null || patientRequestDto.getInfoPersonnel().getEmail().trim().isEmpty() ||
                patientRequestDto.getInfoPersonnel().getNom() == null || patientRequestDto.getInfoPersonnel().getNom().trim().isEmpty() ||
                patientRequestDto.getInfoPersonnel().getPrenom() == null || patientRequestDto.getInfoPersonnel().getPrenom().trim().isEmpty()) {
            throw new IllegalArgumentException("Les informations personnelles (nom, prenom, email) du patient sont obligatoires et ne peuvent pas être vides.");
        }

        // Check for unique email
        if (patientRepository.findPatientByInfoPersonnel_Email(patientRequestDto.getInfoPersonnel().getEmail()).isPresent()) {
            throw new IllegalArgumentException("Un patient avec l'email '" + patientRequestDto.getInfoPersonnel().getEmail() + "' existe déjà.");
        }

        // Convert DTO to entity. Assuming PatientDto.toEntity handles InfoPersonnel conversion.
        Patient patientToSave = PatientRequestDto.toEntity(patientRequestDto);

        // Ensure InfoPersonnel is not null on the entity, even if DTO conversion somehow missed it (though it shouldn't if toEntity is robust)
        if (patientToSave.getInfoPersonnel() == null) {
            patientToSave.setInfoPersonnel(new InfoPersonnel());

        }

        Patient savedPatientEntity = patientRepository.save(patientToSave);
        PatientRequestDto savedPatientRequestDto = PatientRequestDto.fromEntity(savedPatientEntity);

        historiqueActionService.enregistrerAction(
                "Création du patient ID: " + savedPatientRequestDto.getId() +
                        ", Nom: " + (savedPatientRequestDto.getInfoPersonnel() != null ? savedPatientRequestDto.getInfoPersonnel().getNom() : "N/A") + " " +
                        (savedPatientRequestDto.getInfoPersonnel() != null ? savedPatientRequestDto.getInfoPersonnel().getPrenom() : "N/A")
        );

        return savedPatientRequestDto;
    }



    @Override
    @Transactional
    public PatientRequestDto updatePatient(Integer id, PatientRequestDto patientRequestDto) {
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Le patient avec l'ID " + id + " n'existe pas.")); // More specific exception

        // Ensure InfoPersonnel exists or create a new one if it's null on the existing patient
        if (existingPatient.getInfoPersonnel() == null) {
            existingPatient.setInfoPersonnel(new InfoPersonnel());
        }

        // Update personal information if provided in the DTO
        if (patientRequestDto.getInfoPersonnel() != null) {
            InfoPersonnelRequestDto dtoInfo = patientRequestDto.getInfoPersonnel();
            InfoPersonnel existingInfo = existingPatient.getInfoPersonnel();

            if (dtoInfo.getNom() != null && !dtoInfo.getNom().trim().isEmpty()) {
                existingInfo.setNom(dtoInfo.getNom());
            }
            if (dtoInfo.getPrenom() != null && !dtoInfo.getPrenom().trim().isEmpty()) {
                existingInfo.setPrenom(dtoInfo.getPrenom());
            }
            // Email update requires special handling: check for uniqueness and prevent self-conflict
            if (dtoInfo.getEmail() != null && !dtoInfo.getEmail().trim().isEmpty()) {
                if (!dtoInfo.getEmail().equalsIgnoreCase(existingInfo.getEmail())) { // Use equalsIgnoreCase for case-insensitivity
                    if (patientRepository.findPatientByInfoPersonnel_Email(dtoInfo.getEmail()).isPresent()) {
                        throw new IllegalArgumentException("L'email '" + dtoInfo.getEmail() + "' est déjà utilisé par un autre patient.");
                    }
                }
                existingInfo.setEmail(dtoInfo.getEmail());
            }
            if (dtoInfo.getDateNaissance() != null) {
                existingInfo.setDateNaissance(dtoInfo.getDateNaissance());
            }
            if (dtoInfo.getTelephone() != null && !dtoInfo.getTelephone().trim().isEmpty()) {
                existingInfo.setTelephone(dtoInfo.getTelephone());
            }
            if (dtoInfo.getAdresse() != null && !dtoInfo.getAdresse().trim().isEmpty()) {
                existingInfo.setAdresse(dtoInfo.getAdresse());
            }
            if (dtoInfo.getGenre() != null) {
                existingInfo.setGenre(dtoInfo.getGenre());
            }
        }

        Patient updatedPatientEntity = patientRepository.save(existingPatient);
        PatientRequestDto updatedPatientRequestDto = PatientRequestDto.fromEntity(updatedPatientEntity);

        historiqueActionService.enregistrerAction(
                "Mise à jour du patient ID: " + id +
                        ", Nom: " + (updatedPatientRequestDto.getInfoPersonnel() != null ? updatedPatientRequestDto.getInfoPersonnel().getNom() : "N/A") + " " +
                        (updatedPatientRequestDto.getInfoPersonnel() != null ? updatedPatientRequestDto.getInfoPersonnel().getPrenom() : "N/A")
        );

        return updatedPatientRequestDto;
    }



    @Override
    @Transactional
    public List<PatientRequestDto> findAllPatients() {
        List<PatientRequestDto> allPatients = patientRepository.findAll()
                .stream()
                .map(PatientRequestDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction("Affichage de tous les patients.");

        return allPatients;
    }


    @Override
    @Transactional
    public PatientRequestDto findById(Integer id) {
        Patient foundPatientEntity = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Le patient avec l'ID " + id + " n'existe pas.")); // More specific exception

        PatientRequestDto foundPatientRequestDto = PatientRequestDto.fromEntity(foundPatientEntity);

        historiqueActionService.enregistrerAction(
                "Recherche du patient ID: " + id +
                        ", Nom: " + (foundPatientRequestDto.getInfoPersonnel() != null ? foundPatientRequestDto.getInfoPersonnel().getNom() : "N/A") + " " +
                        (foundPatientRequestDto.getInfoPersonnel() != null ? foundPatientRequestDto.getInfoPersonnel().getPrenom() : "N/A")
        );

        return foundPatientRequestDto;
    }


    @Override
    @Transactional
    public void deletePatient(Integer id) {
        // Find patient first to ensure it exists and to get details for logging
        Patient patientToDelete = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Le patient avec l'ID " + id + " n'existe pas et ne peut pas être supprimé.")); // More specific exception



        patientRepository.deleteById(id);

        historiqueActionService.enregistrerAction("Suppression du patient ID: " + id);
    }



    @Override
    @Transactional
    public List<PatientRequestDto> searchPatients(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            List<PatientRequestDto> allPatients = findAllPatients(); // Call the existing method
            historiqueActionService.enregistrerAction("Recherche de tous les patients (terme de recherche vide).");
            return allPatients;
        }
        List<PatientRequestDto> searchedPatients = patientRepository.findPatientBySearchTerm(searchTerm)
                .stream()
                .filter(Objects::nonNull)
                .map(PatientRequestDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction(
                "Recherche de patients avec le terme: '" + searchTerm + "' (nombre de résultats: " + searchedPatients.size() + ")"
        );

        return searchedPatients;
    }


    @Override
    @Transactional
    public List<PatientRequestDto> findPatientByInfoPersonnel_Nom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide pour la recherche.");
        }
        List<PatientRequestDto> patientsByNom = patientRepository.findPatientByInfoPersonnel_Nom(nom)
                .stream()
                .filter(Objects::nonNull)
                .map(PatientRequestDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction(
                "Recherche de patients par nom: '" + nom + "' (nombre de résultats: " + patientsByNom.size() + ")"
        );

        return patientsByNom;
    }


    @Override
    @Transactional
    public PatientRequestDto findPatientByInfoPersonnel_Email(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'email ne peut pas être vide pour la recherche.");
        }
        PatientRequestDto patientByEmail = patientRepository.findPatientByInfoPersonnel_Email(email)
                .map(PatientRequestDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Le patient avec l'email " + email + " n'existe pas.")); // More specific exception

        historiqueActionService.enregistrerAction(
                "Recherche du patient par email: '" + email + "'"
        );

        return patientByEmail;
    }
}