package com.example.GestionClinique.controller.controllerApi;

import com.example.GestionClinique.dto.RequestDto.DossierMedicalRequestDto;
import com.example.GestionClinique.dto.RequestDto.PatientRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.GestionClinique.utils.constants.API_NAME;

@Tag(name = "Gestion des Dossiers Médicaux", description = "API pour la gestion des dossiers médicaux des patients")
@RequestMapping(API_NAME + "/dossierMedical")
public interface DossierMedicalApi {

    @PreAuthorize("hasAnyRole('MEDECIN')")
    @PostMapping(path = "/create/{idPatient}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Créer un dossier médical pour un patient",
            description = "Crée un nouveau dossier médical associé à un patient existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Dossier médical créé avec succès",
                    content = @Content(schema = @Schema(implementation = DossierMedicalRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "Données fournies invalides"),
            @ApiResponse(responseCode = "404", description = "Patient non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    DossierMedicalRequestDto createDossierMedicalForPatient(
            @Parameter(description = "DTO du dossier médical à créer", required = true,
                    content = @Content(schema = @Schema(implementation = DossierMedicalRequestDto.class)))
            @RequestBody DossierMedicalRequestDto dossierMedicalRequestDto,

            @Parameter(description = "ID du patient associé", required = true, example = "1")
            @PathVariable("idPatient") Integer idPatient);



    @PreAuthorize("hasAnyRole('MEDECIN')")
    @PutMapping(path = "/update/{idDossierMedical}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour un dossier médical",
            description = "Modifie les informations d'un dossier médical existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dossier médical mis à jour avec succès",
                    content = @Content(schema = @Schema(implementation = DossierMedicalRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "Données de mise à jour invalides"),
            @ApiResponse(responseCode = "404", description = "Dossier médical non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    DossierMedicalRequestDto updateDossierMedical(
            @Parameter(description = "ID du dossier médical à mettre à jour", required = true, example = "1")
            @PathVariable("idDossierMedical") Integer id,

            @Parameter(description = "DTO contenant les mises à jour", required = true,
                    content = @Content(schema = @Schema(implementation = DossierMedicalRequestDto.class)))
            @RequestBody DossierMedicalRequestDto dossierMedicalRequestDto);



    @PreAuthorize("hasAnyRole('MEDECIN')")
    @GetMapping(path = "/recherche/{idDossierMedical}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir un dossier médical par son ID",
            description = "Récupère les informations complètes d'un dossier médical")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dossier médical trouvé",
                    content = @Content(schema = @Schema(implementation = DossierMedicalRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "ID invalide"),
            @ApiResponse(responseCode = "404", description = "Dossier médical non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    DossierMedicalRequestDto findDossierMedicalById(
            @Parameter(description = "ID du dossier médical", required = true, example = "1")
            @PathVariable("idDossierMedical") Integer id);




    @PreAuthorize("hasAnyRole('MEDECIN')")
    @GetMapping(path = "/recherche/allDossierMedical", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Lister tous les dossiers médicaux",
            description = "Récupère tous les dossiers médicaux enregistrés")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des dossiers retournée",
                    content = @Content(schema = @Schema(implementation = DossierMedicalRequestDto.class))),
            @ApiResponse(responseCode = "204", description = "Aucun dossier trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    List<DossierMedicalRequestDto> findAllDossierMedical();




    @PreAuthorize("hasAnyRole('MEDECIN')")
    @GetMapping(path = "/{idDossierMedical}/patient", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir le patient associé à un dossier",
            description = "Récupère le patient lié à un dossier médical")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient trouvé",
                    content = @Content(schema = @Schema(implementation = PatientRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "ID invalide"),
            @ApiResponse(responseCode = "404", description = "Patient ou dossier non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    PatientRequestDto findPatientByDossierMedicalId(
            @Parameter(description = "ID du dossier médical", required = true, example = "1")
            @PathVariable("idDossierMedical") Integer id);




    @PreAuthorize("hasAnyRole('MEDECIN')")
    @DeleteMapping(path = "/delete/{idDossierMedical}")
    @Operation(summary = "Supprimer un dossier médical",
            description = "Supprime définitivement un dossier médical")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Dossier supprimé avec succès"),
            @ApiResponse(responseCode = "400", description = "ID invalide"),
            @ApiResponse(responseCode = "404", description = "Dossier non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    void deleteDossierMedical(
            @Parameter(description = "ID du dossier à supprimer", required = true, example = "1")
            @PathVariable("idDossierMedical") Integer id);




    @PreAuthorize("hasAnyRole('MEDECIN')")
    @GetMapping(path = "/patient/{idPatient}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir le dossier médical d'un patient",
            description = "Récupère le dossier médical associé à un patient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dossier médical trouvé",
                    content = @Content(schema = @Schema(implementation = DossierMedicalRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "ID patient invalide"),
            @ApiResponse(responseCode = "404", description = "Patient ou dossier non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    DossierMedicalRequestDto findDossierMedicalByPatientId(
            @Parameter(description = "ID du patient", required = true, example = "1")
            @PathVariable("idPatient") Integer id);
}