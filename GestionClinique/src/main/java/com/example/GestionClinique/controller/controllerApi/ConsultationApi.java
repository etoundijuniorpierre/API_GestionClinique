package com.example.GestionClinique.controller.controllerApi;

import com.example.GestionClinique.dto.*;
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

@Tag(name = "Gestion des Consultations", description = "API pour la gestion des consultations médicales")
@RequestMapping(API_NAME + "/consultation")
public interface ConsultationApi {


    @PreAuthorize("hasAnyRole('MEDECIN')")
    @PostMapping(path = "/createConsultation", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Créer une nouvelle consultation",
            description = "Enregistre une nouvelle consultation médicale dans le système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Consultation créée avec succès",
                    content = @Content(schema = @Schema(implementation = ConsultationDto.class))),
            @ApiResponse(responseCode = "400", description = "Données de la consultation invalides"),
            @ApiResponse(responseCode = "404", description = "Ressource nécessaire non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la création")
    })
    ConsultationDto createConsultation(
            @Parameter(description = "Détails de la consultation à créer", required = true,
                    content = @Content(schema = @Schema(implementation = ConsultationDto.class)))
            @RequestBody ConsultationDto consultationDto);



    @PreAuthorize("hasAnyRole('MEDECIN')")
    @PostMapping(path = "/rendezVous/{idRendezVous}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Démarrer une consultation à partir d'un rendez-vous",
            description = "Crée et démarre une consultation liée à un rendez-vous existant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Consultation démarrée avec succès",
                    content = @Content(schema = @Schema(implementation = ConsultationDto.class))),
            @ApiResponse(responseCode = "400", description = "Données de requête invalides"),
            @ApiResponse(responseCode = "404", description = "Rendez-vous introuvable"),
            @ApiResponse(responseCode = "409", description = "Conflit: le rendez-vous est déjà lié ou son statut ne permet pas le démarrage"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    ConsultationDto startConsultation(
            @Parameter(description = "ID du rendez-vous à lier", required = true, example = "1")
            @PathVariable("idRendezVous") Integer idRendezVous,
            @Parameter(description = "Détails de la consultation", required = true,
                    content = @Content(schema = @Schema(implementation = ConsultationDto.class)))
            @RequestBody ConsultationDto consultationDto);



    @PreAuthorize("hasAnyRole('MEDECIN')")
    @PutMapping(path = "/updateConsultation/{idConsultation}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour une consultation",
            description = "Modifie les informations d'une consultation existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consultation mise à jour avec succès",
                    content = @Content(schema = @Schema(implementation = ConsultationDto.class))),
            @ApiResponse(responseCode = "400", description = "Données de mise à jour invalides"),
            @ApiResponse(responseCode = "404", description = "Consultation introuvable"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la mise à jour")
    })
    ConsultationDto updateConsultation(
            @Parameter(description = "ID de la consultation à mettre à jour", required = true, example = "1")
            @PathVariable("idConsultation") Integer id,
            @Parameter(description = "Nouveaux détails de la consultation", required = true,
                    content = @Content(schema = @Schema(implementation = ConsultationDto.class)))
            @RequestBody ConsultationDto consultationDto);



    @PreAuthorize("hasAnyRole('MEDECIN')")
    @GetMapping(path = "/recherche/{idConsultation}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir une consultation par son ID",
            description = "Récupère les détails complets d'une consultation spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Consultation trouvée et retournée",
                    content = @Content(schema = @Schema(implementation = ConsultationDto.class))),
            @ApiResponse(responseCode = "400", description = "ID de consultation invalide"),
            @ApiResponse(responseCode = "404", description = "Consultation introuvable"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    ConsultationDto findById(
            @Parameter(description = "ID de la consultation à récupérer", required = true, example = "1")
            @PathVariable("idConsultation") Integer id);



    @PreAuthorize("hasAnyRole('MEDECIN')")
    @GetMapping(path = "/recherche/allConsultation", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Lister toutes les consultations",
            description = "Récupère la liste complète des consultations enregistrées")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des consultations retournée avec succès",
                    content = @Content(schema = @Schema(implementation = ConsultationDto.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    List<ConsultationDto> findAll();



    @PreAuthorize("hasAnyRole('MEDECIN')")
    @GetMapping(path = "/{idConsultation}/dossierMedical", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir le dossier médical lié",
            description = "Récupère le dossier médical associé à une consultation spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dossier médical trouvé et retourné",
                    content = @Content(schema = @Schema(implementation = DossierMedicalDto.class))),
            @ApiResponse(responseCode = "400", description = "ID de consultation invalide"),
            @ApiResponse(responseCode = "404", description = "Dossier médical ou consultation introuvable"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    DossierMedicalDto findDossierMedicalByConsultationId(
            @Parameter(description = "ID de la consultation", required = true, example = "1")
            @PathVariable("idConsultation") Integer idConsultation);



    @PreAuthorize("hasAnyRole('MEDECIN')")
    @GetMapping(path = "/{idConsultation}/rendezVous", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir le rendez-vous lié",
            description = "Récupère le rendez-vous associé à une consultation spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rendez-vous trouvé et retourné",
                    content = @Content(schema = @Schema(implementation = RendezVousDto.class))),
            @ApiResponse(responseCode = "400", description = "ID de consultation invalide"),
            @ApiResponse(responseCode = "404", description = "Rendez-vous ou consultation introuvable"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    RendezVousDto findRendezVousByConsultationId(
            @Parameter(description = "ID de la consultation", required = true, example = "1")
            @PathVariable("idConsultation") Integer idConsultation);



    @PreAuthorize("hasAnyRole('MEDECIN')")
    @DeleteMapping(path = "/delete/{idConsultation}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Supprimer une consultation",
            description = "Supprime définitivement une consultation du système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Consultation supprimée avec succès"),
            @ApiResponse(responseCode = "400", description = "ID de consultation invalide"),
            @ApiResponse(responseCode = "404", description = "Consultation introuvable"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la suppression")
    })
    void deleteById(
            @Parameter(description = "ID de la consultation à supprimer", required = true, example = "1")
            @PathVariable("idConsultation") Integer id);



    @PreAuthorize("hasAnyRole('MEDECIN')")
    @PostMapping(path = "/prescription/addPrescription/{idConsultation}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Ajouter une prescription",
            description = "Ajoute une prescription médicale à une consultation existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Prescription ajoutée avec succès",
                    content = @Content(schema = @Schema(implementation = ConsultationDto.class))),
            @ApiResponse(responseCode = "400", description = "Données de prescription invalides"),
            @ApiResponse(responseCode = "404", description = "Consultation introuvable"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de l'ajout")
    })
    ConsultationDto addPrescriptionToConsultation(
            @Parameter(description = "ID de la consultation", required = true, example = "1")
            @PathVariable("idConsultation") Integer id,
            @Parameter(description = "Détails de la prescription", required = true,
                    content = @Content(schema = @Schema(implementation = PrescriptionDto.class)))
            @RequestBody PrescriptionDto prescriptionDto);



    @PreAuthorize("hasAnyRole('MEDECIN')")
    @GetMapping(path = "/prescription/{idConsultation}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir les prescriptions d'une consultation",
            description = "Récupère toutes les prescriptions associées à une consultation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prescriptions trouvées et retournées",
                    content = @Content(schema = @Schema(implementation = PrescriptionDto.class))),
            @ApiResponse(responseCode = "400", description = "ID de consultation invalide"),
            @ApiResponse(responseCode = "404", description = "Aucune prescription trouvée pour cette consultation"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    List<PrescriptionDto> findPrescriptionsByConsultationId(
            @Parameter(description = "ID de la consultation", required = true, example = "1")
            @PathVariable("idConsultation") Integer id);
}