package com.example.GestionClinique.controller;


import com.example.GestionClinique.dto.ResponseDto.DossierMedicalResponseDto;
import com.example.GestionClinique.dto.RequestDto.DossierMedicalRequestDto;
import com.example.GestionClinique.dto.ResponseDto.PatientResponseDto;
import com.example.GestionClinique.mapper.DossierMedicalMapper;
import com.example.GestionClinique.mapper.PatientMapper;
import com.example.GestionClinique.model.entity.*;

import com.example.GestionClinique.service.DossierMedicalService;
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


import static com.example.GestionClinique.configuration.utils.Constants.API_NAME;


@Tag(name = "Gestion des Dossiers Médicaux", description = "API pour la gestion des dossiers médicaux des patients")
@RequestMapping(API_NAME + "/dossierMedical")
@RestController // Mark as REST Controller
public class DossierMedicalController {

    private final DossierMedicalService dossierMedicalService;
    private final DossierMedicalMapper dossierMedicalMapper;
    private final PatientMapper patientMapper;

    @Autowired
    public DossierMedicalController(DossierMedicalService dossierMedicalService,
                                    DossierMedicalMapper dossierMedicalMapper,
                                    PatientMapper patientMapper) {
        this.dossierMedicalService = dossierMedicalService;
        this.dossierMedicalMapper = dossierMedicalMapper;
        this.patientMapper = patientMapper;
    }



@PreAuthorize("hasAnyRole('MEDECIN', 'SECRETAIRE')")
    @PostMapping(path = "/create/{idPatient}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Créer un dossier médical pour un patient",
            description = "Crée un nouveau dossier médical associé à un patient existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Dossier médical créé avec succès",
                    content = @Content(schema = @Schema(implementation = DossierMedicalResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Données fournies invalides ou patient a déjà un dossier"),
            @ApiResponse(responseCode = "404", description = "Patient non trouvé"),
            @ApiResponse(responseCode = "409", description = "Conflit: Le patient a déjà un dossier médical"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<DossierMedicalResponseDto> createDossierMedicalForPatient(
            @Parameter(description = "DTO du dossier médical à créer", required = true,
                    content = @Content(schema = @Schema(implementation = DossierMedicalRequestDto.class)))
            @Valid @RequestBody DossierMedicalRequestDto dossierMedicalRequestDto,

            @Parameter(description = "ID du patient associé", required = true, example = "1")
            @PathVariable("idPatient") Long idPatient) {

        DossierMedical dossierMedicalToCreate = dossierMedicalMapper.toEntity(dossierMedicalRequestDto);
        DossierMedical createdDossierMedical = dossierMedicalService.createDossierMedicalForPatient(idPatient, dossierMedicalToCreate);
        return new ResponseEntity<>(dossierMedicalMapper.toDto(createdDossierMedical), HttpStatus.CREATED);

    }



@PreAuthorize("hasAnyRole('MEDECIN', 'SECRETAIRE')")
    @PutMapping(path = "/update/{idDossierMedical}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour un dossier médical",
            description = "Modifie les informations d'un dossier médical existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dossier médical mis à jour avec succès",
                    content = @Content(schema = @Schema(implementation = DossierMedicalResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Données de mise à jour invalides"),
            @ApiResponse(responseCode = "404", description = "Dossier médical non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<DossierMedicalResponseDto> updateDossierMedical(
            @Parameter(description = "ID du dossier médical à mettre à jour", required = true, example = "1")
            @PathVariable("idDossierMedical") Long id,

            @Parameter(description = "DTO contenant les mises à jour", required = true,
                    content = @Content(schema = @Schema(implementation = DossierMedicalRequestDto.class)))
            @Valid @RequestBody DossierMedicalRequestDto dossierMedicalRequestDto) {

        DossierMedical existingDossierMedical = dossierMedicalService.findDossierMedicalById(id);
        dossierMedicalMapper.updateEntityFromDto(dossierMedicalRequestDto, existingDossierMedical);
        DossierMedical updatedDossierMedical = dossierMedicalService.updateDossierMedical(id, existingDossierMedical);
        return ResponseEntity.ok(dossierMedicalMapper.toDto(updatedDossierMedical));
    }



@PreAuthorize("hasAnyRole('MEDECIN', 'SECRETAIRE')")
    @GetMapping(path = "/recherche/{idDossierMedical}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir un dossier médical par son ID",
            description = "Récupère les informations complètes d'un dossier médical")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dossier médical trouvé",
                    content = @Content(schema = @Schema(implementation = DossierMedicalResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "ID invalide"),
            @ApiResponse(responseCode = "404", description = "Dossier médical non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<DossierMedicalResponseDto> findDossierMedicalById(
            @Parameter(description = "ID du dossier médical", required = true, example = "1")
            @PathVariable("idDossierMedical") Long id) {

        DossierMedical dossierMedical = dossierMedicalService.findDossierMedicalById(id);
        return ResponseEntity.ok(dossierMedicalMapper.toDto(dossierMedical));
    }



@PreAuthorize("hasAnyRole('MEDECIN', 'SECRETAIRE')")
    @GetMapping(path = "/recherche/allDossierMedical", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Lister tous les dossiers médicaux",
            description = "Récupère tous les dossiers médicaux enregistrés")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des dossiers retournée",
                    content = @Content(schema = @Schema(implementation = DossierMedicalResponseDto.class))),
            @ApiResponse(responseCode = "204", description = "Aucun dossier trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<DossierMedicalResponseDto>> findAllDossierMedical() {
        List<DossierMedical> dossiers = dossierMedicalService.findAllDossierMedical();
        if (dossiers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(dossierMedicalMapper.toDtoList(dossiers));
    }



@PreAuthorize("hasAnyRole('MEDECIN', 'SECRETAIRE')")
    @GetMapping(path = "/{idDossierMedical}/patient", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir le patient associé à un dossier",
            description = "Récupère le patient lié à un dossier médical")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient trouvé",
                    content = @Content(schema = @Schema(implementation = PatientResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "ID invalide"),
            @ApiResponse(responseCode = "404", description = "Patient ou dossier non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<PatientResponseDto> findPatientByDossierMedicalId(
            @Parameter(description = "ID du dossier médical", required = true, example = "1")
            @PathVariable("idDossierMedical") Long id) {

        Patient patient = dossierMedicalService.findPatientByDossierMedicalId(id);
        return ResponseEntity.ok(patientMapper.toDto(patient));

    }



@PreAuthorize("hasAnyRole('MEDECIN', 'SECRETAIRE')")
    @DeleteMapping(path = "/delete/{idDossierMedical}")
    @Operation(summary = "Supprimer un dossier médical",
            description = "Supprime définitivement un dossier médical")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Dossier supprimé avec succès"),
            @ApiResponse(responseCode = "400", description = "ID invalide"),
            @ApiResponse(responseCode = "404", description = "Dossier non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<Void> deleteDossierMedical(
            @Parameter(description = "ID du dossier à supprimer", required = true, example = "1")
            @PathVariable("idDossierMedical") Long id) {

        dossierMedicalService.deleteDossierMedicalById(id);
        return ResponseEntity.noContent().build();

    }



@PreAuthorize("hasAnyRole('MEDECIN', 'SECRETAIRE')")
    @GetMapping(path = "/patient/{idPatient}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir le dossier médical d'un patient",
            description = "Récupère le dossier médical associé à un patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dossier médical trouvé",
                    content = @Content(schema = @Schema(implementation = DossierMedicalResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "ID patient invalide"),
            @ApiResponse(responseCode = "404", description = "Patient ou dossier non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<DossierMedicalResponseDto> findDossierMedicalByPatientId(
            @Parameter(description = "ID du patient", required = true, example = "1")
            @PathVariable("idPatient") Long idPatient) {

        DossierMedical dossierMedical = dossierMedicalService.findDossierMedicalByPatientId(idPatient);
        return ResponseEntity.ok(dossierMedicalMapper.toDto(dossierMedical));

    }
}