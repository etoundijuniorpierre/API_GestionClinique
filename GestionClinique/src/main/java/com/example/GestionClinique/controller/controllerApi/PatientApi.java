package com.example.GestionClinique.controller.controllerApi;

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

@Tag(name = "Gestion des Patients", description = "API pour la gestion des patients de la clinique")
@RequestMapping(API_NAME + "/patients")
public interface PatientApi {

    @PreAuthorize("hasAnyRole('SECRETAIRE')")
    @PostMapping(path = "/create",consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Créer un nouveau patient",
            description = "Enregistre un nouveau patient dans le système avec ses informations personnelles et médicales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Patient créé avec succès",
                    content = @Content(schema = @Schema(implementation = PatientRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "Données du patient invalides ou incomplètes"),
            @ApiResponse(responseCode = "404", description = "Ressource requise non trouvée (ex: médecin référent)"),
            @ApiResponse(responseCode = "409", description = "Conflit: patient existe déjà (email ou numéro unique)"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la création")
    })
    PatientRequestDto createPatient(
            @Parameter(description = "Détails du patient à créer", required = true,
                    content = @Content(schema = @Schema(implementation = PatientRequestDto.class)))
            @RequestBody PatientRequestDto patientRequestDto);




    @PreAuthorize("hasAnyRole('SECRETAIRE', 'ADMIN', 'MEDECIN')")
    @PutMapping(path = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour un patient",
            description = "Modifie les informations d'un patient existant identifié par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient mis à jour avec succès",
                    content = @Content(schema = @Schema(implementation = PatientRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "Données de mise à jour invalides"),
            @ApiResponse(responseCode = "404", description = "Patient introuvable"),
            @ApiResponse(responseCode = "409", description = "Conflit: nouvelle email déjà utilisée"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la mise à jour")
    })
    PatientRequestDto updatePatient(
            @Parameter(description = "ID du patient à mettre à jour", required = true, example = "1")
            @PathVariable("id") Integer id,
            @Parameter(description = "Nouvelles informations du patient", required = true,
                    content = @Content(schema = @Schema(implementation = PatientRequestDto.class)))
            @RequestBody PatientRequestDto patientRequestDto);




    @PreAuthorize("hasAnyRole('SECRETAIRE', 'ADMIN', 'MEDECIN')")
    @GetMapping(path = "/recherche/allPatient" ,produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Lister tous les patients",
            description = "Récupère la liste complète des patients enregistrés dans le système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des patients retournée avec succès",
                    content = @Content(schema = @Schema(implementation = PatientRequestDto.class))),
            @ApiResponse(responseCode = "204", description = "Aucun patient trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    List<PatientRequestDto> findAllPatients();



    @PreAuthorize("hasAnyRole('SECRETAIRE', 'ADMIN', 'MEDECIN')")
    @GetMapping(path = "/recherche/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir un patient par son ID",
            description = "Récupère les détails complets d'un patient spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient trouvé et retourné",
                    content = @Content(schema = @Schema(implementation = PatientRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "ID de patient invalide"),
            @ApiResponse(responseCode = "404", description = "Patient introuvable"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    PatientRequestDto findById(
            @Parameter(description = "ID du patient à récupérer", required = true, example = "1")
            @PathVariable("id") Integer id);



    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping(path = "/delete/{id}")
    @Operation(summary = "Supprimer un patient",
            description = "Supprime définitivement un patient du système (archivage selon politique de rétention)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Patient supprimé avec succès"),
            @ApiResponse(responseCode = "400", description = "ID de patient invalide"),
            @ApiResponse(responseCode = "404", description = "Patient introuvable"),
            @ApiResponse(responseCode = "403", description = "Opération non autorisée (dossier médical existant)"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la suppression")
    })
    void deletePatient(
            @Parameter(description = "ID du patient à supprimer", required = true, example = "1")
            @PathVariable("id") Integer id);




    @PreAuthorize("hasAnyRole('SECRETAIRE', 'ADMIN', 'MEDECIN')")
    @GetMapping(path = "/recherche/term/{term}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher des patients",
            description = "Recherche des patients par terme (nom, prénom, email, téléphone, etc.)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Résultats de recherche retournés",
                    content = @Content(schema = @Schema(implementation = PatientRequestDto.class))),
            @ApiResponse(responseCode = "204", description = "Aucun patient correspondant trouvé"),
            @ApiResponse(responseCode = "400", description = "Terme de recherche trop court ou invalide"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la recherche")
    })
    List<PatientRequestDto> searchPatients(
            @Parameter(description = "Terme de recherche (minimum 3 caractères)", required = true, example = "Dupont")
            @PathVariable("term") String searchTerm);





    @PreAuthorize("hasAnyRole('SECRETAIRE', 'ADMIN', 'MEDECIN')")
    @GetMapping(path = "/recherche/nom/{nom}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher par nom exact",
            description = "Trouve tous les patients portant exactement le nom spécifié")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patients trouvés et retournés",
                    content = @Content(schema = @Schema(implementation = PatientRequestDto.class))),
            @ApiResponse(responseCode = "204", description = "Aucun patient avec ce nom exact"),
            @ApiResponse(responseCode = "400", description = "Nom invalide (doit contenir seulement des lettres)"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la recherche")
    })
    List<PatientRequestDto> findPatientByInfoPersonnel_Nom(
            @Parameter(description = "Nom exact du patient (case insensitive)", required = true, example = "Dupont")
            @PathVariable("nom") String nom);



    @PreAuthorize("hasAnyRole('SECRETAIRE', 'ADMIN', 'MEDECIN')")
    @GetMapping(path = "/recherche/email/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher par email exact",
            description = "Trouve un patient unique par son adresse email exacte")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient trouvé et retourné",
                    content = @Content(schema = @Schema(implementation = PatientRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "Format d'email invalide"),
            @ApiResponse(responseCode = "404", description = "Aucun patient avec cet email"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la recherche")
    })
    PatientRequestDto findPatientByInfoPersonnel_Email(
            @Parameter(description = "Email exact du patient", required = true, example = "patient@example.com")
            @PathVariable("email") String email);
}