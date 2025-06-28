package com.example.GestionClinique.controller;


import com.example.GestionClinique.dto.RequestDto.PatientRequestDto;
import com.example.GestionClinique.dto.ResponseDto.PatientResponseDto;
import com.example.GestionClinique.mapper.PatientMapper;
import com.example.GestionClinique.model.entity.Patient;
import com.example.GestionClinique.service.PatientService;
import com.example.GestionClinique.utils.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Gestion des Patients", description = "API pour la gestion des patients de la clinique")
@RequestMapping(Constants.API_NAME + "/patients")
@RestController // This annotation is crucial for Spring to recognize it as a controller
public class PatientController {

    private final PatientService patientService;
    private final PatientMapper patientMapper;

    @Autowired
    public PatientController(PatientService patientService, PatientMapper patientMapper) {
        this.patientService = patientService;
        this.patientMapper = patientMapper;
    }



    @PreAuthorize("hasAnyRole('SECRETAIRE')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Créer un nouveau patient",
            description = "Enregistre un nouveau patient dans le système avec ses informations personnelles et médicales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Patient créé avec succès",
                    content = @Content(schema = @Schema(implementation = PatientResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Données du patient invalides ou incomplètes"),
            @ApiResponse(responseCode = "409", description = "Conflit: patient existe déjà (email unique)"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la création")
    })
    public ResponseEntity<PatientResponseDto> createPatient(
            @Parameter(description = "Détails du patient à créer", required = true)
            @Valid @RequestBody PatientRequestDto patientRequestDto) {
        // Map DTO to Entity for service layer
        Patient patientToCreate = patientMapper.toEntity(patientRequestDto);
        // Call service to create entity
        Patient createdPatient = patientService.createPatient(patientToCreate);
        // Map created Entity back to Response DTO
        PatientResponseDto responseDto = patientMapper.toDto(createdPatient);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED); // Return 201 Created
    }



    @PreAuthorize("hasAnyRole('SECRETAIRE', 'ADMIN', 'MEDECIN')")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour un patient",
            description = "Modifie les informations d'un patient existant identifié par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient mis à jour avec succès",
                    content = @Content(schema = @Schema(implementation = PatientResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Données de mise à jour invalides"),
            @ApiResponse(responseCode = "404", description = "Patient introuvable"),
            @ApiResponse(responseCode = "409", description = "Conflit: nouvelle email déjà utilisée"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la mise à jour")
    })
    public ResponseEntity<PatientResponseDto> updatePatient(
            @Parameter(description = "ID du patient à mettre à jour", required = true, example = "1")
            @PathVariable("id") Long id,
            @Parameter(description = "Nouvelles informations du patient", required = true)
            @Valid @RequestBody PatientRequestDto patientRequestDto) {
        // Fetch existing entity to update
        Patient existingPatient = patientService.findById(id);
        // Update existing entity from DTO (MapStruct handles null checks for fields not in DTO)
        patientMapper.updateEntityFromDto(patientRequestDto, existingPatient);
        // Pass the updated entity to the service
        Patient updatedPatient = patientService.updatePatient(id, existingPatient);
        // Map updated Entity back to Response DTO
        return ResponseEntity.ok(patientMapper.toDto(updatedPatient)); // Return 200 OK
    }



    @PreAuthorize("hasAnyRole('SECRETAIRE', 'ADMIN', 'MEDECIN')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Lister tous les patients",
            description = "Récupère la liste complète des patients enregistrés dans le système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des patients retournée avec succès",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PatientResponseDto.class)))),
            @ApiResponse(responseCode = "204", description = "Aucun patient trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    public ResponseEntity<List<PatientResponseDto>> findAllPatients() {
        List<Patient> patients = patientService.findAllPatients();
        if (patients.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content if no patients
        }
        return ResponseEntity.ok(patientMapper.toDtoList(patients)); // Map to DTO list
    }



    @PreAuthorize("hasAnyRole('SECRETAIRE', 'ADMIN', 'MEDECIN')")
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir un patient par son ID",
            description = "Récupère les détails complets d'un patient spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient trouvé et retourné",
                    content = @Content(schema = @Schema(implementation = PatientResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "ID de patient invalide"),
            @ApiResponse(responseCode = "404", description = "Patient introuvable"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    public ResponseEntity<PatientResponseDto> findById(
            @Parameter(description = "ID du patient à récupérer", required = true, example = "1")
            @PathVariable("id") Long id) {
        Patient patient = patientService.findById(id);
        return ResponseEntity.ok(patientMapper.toDto(patient)); // Map to DTO
    }



    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping(path = "/{id}")
    @Operation(summary = "Supprimer un patient",
            description = "Supprime définitivement un patient du système (archivage selon politique de rétention)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Patient supprimé avec succès"),
            @ApiResponse(responseCode = "400", description = "ID de patient invalide"),
            @ApiResponse(responseCode = "404", description = "Patient introuvable"),
            @ApiResponse(responseCode = "403", description = "Opération non autorisée (dossier médical existant)"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la suppression")
    })
    public ResponseEntity<Void> deletePatient(
            @Parameter(description = "ID du patient à supprimer", required = true, example = "1")
            @PathVariable("id") Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }



    @PreAuthorize("hasAnyRole('SECRETAIRE', 'ADMIN', 'MEDECIN')")
    @GetMapping(path = "/search/{searchTerm}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher des patients",
            description = "Recherche des patients par terme (nom, prénom, email, téléphone, etc.)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Résultats de recherche retournés",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PatientResponseDto.class)))),
            @ApiResponse(responseCode = "204", description = "Aucun patient correspondant trouvé"),
            @ApiResponse(responseCode = "400", description = "Terme de recherche trop court ou invalide"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la recherche")
    })
    public ResponseEntity<List<PatientResponseDto>> searchPatients(
            @Parameter(description = "Terme de recherche (minimum 3 caractères)", required = true, example = "Dupont")
            @PathVariable("searchTerm") String searchTerm) {
        List<Patient> patients = patientService.searchPatients(searchTerm);
        if (patients.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(patientMapper.toDtoList(patients));
    }



    @PreAuthorize("hasAnyRole('SECRETAIRE', 'ADMIN', 'MEDECIN')")
    @GetMapping(path = "/nom/{nom}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher par nom exact",
            description = "Trouve tous les patients portant exactement le nom spécifié")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patients trouvés et retournés",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = PatientResponseDto.class)))),
            @ApiResponse(responseCode = "204", description = "Aucun patient avec ce nom exact"),
            @ApiResponse(responseCode = "400", description = "Nom invalide (doit contenir seulement des lettres)"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la recherche")
    })
    public ResponseEntity<List<PatientResponseDto>> findPatientByNom(
            @Parameter(description = "Nom exact du patient (case insensitive)", required = true, example = "Dupont")
            @PathVariable("nom") String nom) {
        List<Patient> patients = patientService.findPatientByNom(nom);
        if (patients.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(patientMapper.toDtoList(patients));
    }



    @PreAuthorize("hasAnyRole('SECRETAIRE', 'ADMIN', 'MEDECIN')")
    @GetMapping(path = "/email/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher par email exact",
            description = "Trouve un patient unique par son adresse email exacte")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient trouvé et retourné",
                    content = @Content(schema = @Schema(implementation = PatientResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Format d'email invalide"),
            @ApiResponse(responseCode = "404", description = "Aucun patient avec cet email"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la recherche")
    })
    public ResponseEntity<PatientResponseDto> findPatientByEmail(
            @Parameter(description = "Email exact du patient", required = true, example = "patient@example.com")
            @PathVariable("email") String email) {
        Patient patient = patientService.findPatientByEmail(email);
        return ResponseEntity.ok(patientMapper.toDto(patient));
    }
}
