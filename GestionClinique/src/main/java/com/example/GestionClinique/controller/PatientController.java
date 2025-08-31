package com.example.GestionClinique.controller;


import com.example.GestionClinique.dto.RequestDto.PatientRequestDto;
import com.example.GestionClinique.dto.ResponseDto.PatientResponseDto;
import com.example.GestionClinique.dto.ResponseDto.RendezVousResponseDto;
import com.example.GestionClinique.mapper.PatientMapper;
import com.example.GestionClinique.mapper.RendezVousMapper;
import com.example.GestionClinique.model.entity.Patient;
import com.example.GestionClinique.model.entity.RendezVous;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import com.example.GestionClinique.service.PatientService;
import com.example.GestionClinique.service.RendezVousService;
import com.example.GestionClinique.configuration.utils.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
    private final RendezVousService rendezVousService;
    private final RendezVousMapper rendezVousMapper;

    @Autowired
    public PatientController(PatientService patientService, PatientMapper patientMapper, RendezVousService rendezVousService, RendezVousMapper rendezVousMapper) {
        this.patientService = patientService;
        this.patientMapper = patientMapper;
        this.rendezVousService = rendezVousService;
        this.rendezVousMapper = rendezVousMapper;
    }



    @PreAuthorize("hasAnyRole('SECRETAIRE', 'ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Créer un nouveau patient",
            description = "Enregistre un nouveau patient dans le système avec ses informations personnelles et un dossier médical complet.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Patient et dossier médical créés avec succès",
                    content = @Content(schema = @Schema(implementation = PatientResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Requête invalide (données manquantes ou format incorrect)"),
            @ApiResponse(responseCode = "401", description = "Non autorisé"),
            @ApiResponse(responseCode = "403", description = "Accès interdit"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<PatientResponseDto> createPatient(
            @Parameter(description = "Détails du patient et de son dossier médical à créer", required = true)
            @Valid @RequestBody PatientRequestDto patientRequestDto) {
        Patient patientToCreate = patientMapper.toEntity(patientRequestDto);

        Patient createdPatient = patientService.createPatient(patientToCreate);

        PatientResponseDto responseDto = patientMapper.toDto(createdPatient);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }



@PreAuthorize("hasAnyRole('SECRETAIRE', 'ADMIN')")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour un patient",
            description = "Modifie les informations d'un patient existant identifié par son ID")
    public ResponseEntity<PatientResponseDto> updatePatient(
            @Parameter(description = "ID du patient à mettre à jour", required = true, example = "1")
            @PathVariable("id") Long id,
            @Parameter(description = "Nouvelles informations du patient", required = true)
            @Valid @RequestBody PatientRequestDto patientRequestDto) {

        Patient existingPatient = patientService.findById(id);
        patientMapper.updateEntityFromDto(patientRequestDto, existingPatient);
        Patient updatedPatient = patientService.updatePatient(id, existingPatient);
        return ResponseEntity.ok(patientMapper.toDto(updatedPatient)); // Return 200 OK
    }



@PreAuthorize("hasAnyRole('SECRETAIRE', 'ADMIN', 'MEDECIN')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Lister tous les patients",
            description = "Récupère la liste complète des patients enregistrés dans le système")
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
    public ResponseEntity<PatientResponseDto> findById(
            @Parameter(description = "ID du patient à récupérer", required = true, example = "1")
            @PathVariable("id") Long id) {
        Patient patient = patientService.findById(id);
        return ResponseEntity.ok(patientMapper.toDto(patient)); // Map to DTO
    }



    @PreAuthorize("hasAnyRole('SECRETAIRE', 'ADMIN')")
    @DeleteMapping(path = "/{id}")
    @Operation(summary = "Supprimer un patient",
            description = "Supprime définitivement un patient du système (archivage selon politique de rétention)")
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
    public ResponseEntity<PatientResponseDto> findPatientByEmail(
            @Parameter(description = "Email exact du patient", required = true, example = "patient@example.com")
            @PathVariable("email") String email) {
        Patient patient = patientService.findPatientByEmail(email);
        return ResponseEntity.ok(patientMapper.toDto(patient));
    }


    @PreAuthorize("hasAnyRole('SECRETAIRE', 'ADMIN', 'MEDECIN')")
    @GetMapping("/rendezvous/search")
    @Operation(summary = "Rechercher les rendez-vous d'un patient par terme",
            description = "Recherche les rendez-vous d'un patient en utilisant un terme qui peut correspondre à son nom, prénom, email ou ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des rendez-vous récupérée avec succès",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RendezVousResponseDto.class))),
            @ApiResponse(responseCode = "204", description = "Aucun rendez-vous trouvé pour le terme de recherche donné"),
            @ApiResponse(responseCode = "400", description = "Paramètre de requête manquant ou invalide")
    })
    public ResponseEntity<List<RendezVousResponseDto>> getRendezVousByPatientSearchTerm(
            @Parameter(description = "Terme de recherche (nom, prénom, email ou ID du patient)", required = true, example = "Doe")
            @RequestParam String patientSearchTerm) {
        List<RendezVous> rendezVousEntities = patientService.findRendezVousByPatientSearchTerm(patientSearchTerm);
        List<RendezVousResponseDto> rendezVousDtos = rendezVousMapper.toDtoList(rendezVousEntities);

        if (rendezVousDtos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(rendezVousDtos);
    }


    @PreAuthorize("hasAnyRole('SECRETAIRE', 'ADMIN', 'MEDECIN')")
    @GetMapping("/rendezvous/status")
    @Operation(summary = "Récupérer les rendez-vous d'un patient par son nom et statut",
            description = "Permet de filtrer les rendez-vous d'un patient en spécifiant une partie de son nom (nom ou prénom) et un statut de rendez-vous.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des rendez-vous récupérée avec succès",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RendezVousResponseDto.class))),
            @ApiResponse(responseCode = "204", description = "Aucun rendez-vous trouvé pour les critères donnés"),
            @ApiResponse(responseCode = "400", description = "Paramètre de requête manquant ou invalide")
    })
    public ResponseEntity<List<RendezVousResponseDto>> getRendezVousForPatientByStatus(
            @Parameter(description = "Nom ou prénom du patient (recherche partielle)", required = true, example = "Jean")
            @RequestParam String patientName,
            @Parameter(description = "Statut du rendez-vous (ex: CONFIRME, ANNULE, TERMINE)", required = true, example = "CONFIRME")
            @RequestParam StatutRDV statut) {
        List<RendezVous> rendezVousEntities = patientService.findRendezVousForPatientByStatus(patientName, statut);
        List<RendezVousResponseDto> rendezVousDtos = rendezVousMapper.toDtoList(rendezVousEntities);
        if (rendezVousDtos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(rendezVousDtos);
    }
}
