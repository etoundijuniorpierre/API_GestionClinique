package com.example.GestionClinique.controller.controllerApi;

import com.example.GestionClinique.dto.RequestDto.PrescriptionRequestDto;
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

@Tag(name = "Gestion des Prescriptions", description = "API pour la gestion des prescriptions médicales")
@RequestMapping(API_NAME + "/prescriptions")
public interface PrescriptionApi {

    @PreAuthorize("hasAnyRole('MEDECIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Créer une prescription",
            description = "Enregistre une nouvelle prescription médicale dans le système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Prescription créée avec succès",
                    content = @Content(schema = @Schema(implementation = PrescriptionRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "Données de prescription invalides"),
            @ApiResponse(responseCode = "404", description = "Patient ou médecin non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    PrescriptionRequestDto createPrescription(
            @Parameter(description = "Détails de la prescription à créer", required = true,
                    content = @Content(schema = @Schema(implementation = PrescriptionRequestDto.class)))
            @RequestBody PrescriptionRequestDto prescriptionRequestDto);





    @PreAuthorize("hasAnyRole('MEDECIN')")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour une prescription",
            description = "Modifie les informations d'une prescription existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prescription mise à jour avec succès",
                    content = @Content(schema = @Schema(implementation = PrescriptionRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "Données de mise à jour invalides"),
            @ApiResponse(responseCode = "404", description = "Prescription introuvable"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la mise à jour")
    })
    PrescriptionRequestDto updatePrescription(
            @Parameter(description = "ID de la prescription à mettre à jour", required = true, example = "1")
            @PathVariable("id") Integer id,
            @Parameter(description = "Nouveaux détails de la prescription", required = true,
                    content = @Content(schema = @Schema(implementation = PrescriptionRequestDto.class)))
            @RequestBody PrescriptionRequestDto prescriptionRequestDto);





    @PreAuthorize("hasAnyRole('MEDECIN')")
    @GetMapping(path = "/recherche/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir une prescription par son ID",
            description = "Récupère les détails complets d'une prescription spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prescription trouvée et retournée",
                    content = @Content(schema = @Schema(implementation = PrescriptionRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "ID de prescription invalide"),
            @ApiResponse(responseCode = "404", description = "Prescription introuvable"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    PrescriptionRequestDto findById(
            @Parameter(description = "ID de la prescription à récupérer", required = true, example = "1")
            @PathVariable("id") Integer id);





    @PreAuthorize("hasAnyRole('MEDECIN')")
    @GetMapping(path = "/recherche/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Lister toutes les prescriptions",
            description = "Récupère la liste complète des prescriptions enregistrées")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des prescriptions retournée avec succès",
                    content = @Content(schema = @Schema(implementation = PrescriptionRequestDto.class))),
            @ApiResponse(responseCode = "204", description = "Aucune prescription trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    List<PrescriptionRequestDto> findAllPrescription();




    @PreAuthorize("hasAnyRole('MEDECIN')")
    @DeleteMapping(path = "/delete/{id}")
    @Operation(summary = "Supprimer une prescription",
            description = "Supprime définitivement une prescription du système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Prescription supprimée avec succès"),
            @ApiResponse(responseCode = "400", description = "ID de prescription invalide"),
            @ApiResponse(responseCode = "404", description = "Prescription introuvable"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la suppression")
    })
    void deletePrescription(
            @Parameter(description = "ID de la prescription à supprimer", required = true, example = "1")
            @PathVariable("id") Integer id);






    @PreAuthorize("hasAnyRole('MEDECIN')")
    @GetMapping(path = "/recherche/{medecinId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir les prescriptions par médecin",
            description = "Récupère toutes les prescriptions rédigées par un médecin spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prescriptions trouvées et retournées",
                    content = @Content(schema = @Schema(implementation = PrescriptionRequestDto.class))),
            @ApiResponse(responseCode = "204", description = "Aucune prescription trouvée pour ce médecin"),
            @ApiResponse(responseCode = "400", description = "ID de médecin invalide"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    List<PrescriptionRequestDto> findPrescriptionByMedecinId(
            @Parameter(description = "ID du médecin", required = true, example = "1")
            @PathVariable("medecinId") Integer id);






    @PreAuthorize("hasAnyRole('MEDECIN')")
    @GetMapping(path = "/recherche/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir les prescriptions par patient",
            description = "Récupère toutes les prescriptions associées à un patient spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prescriptions trouvées et retournées",
                    content = @Content(schema = @Schema(implementation = PrescriptionRequestDto.class))),
            @ApiResponse(responseCode = "204", description = "Aucune prescription trouvée pour ce patient"),
            @ApiResponse(responseCode = "400", description = "ID de patient invalide"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    List<PrescriptionRequestDto> findPrescriptionByPatientId(
            @Parameter(description = "ID du patient", required = true, example = "1")
            @PathVariable("patientId") Integer id);






    @PreAuthorize("hasAnyRole('MEDECIN')")
    @GetMapping(path = "/recherche/{consultationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir les prescriptions par consultation",
            description = "Récupère toutes les prescriptions associées à une consultation spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Prescriptions trouvées et retournées",
                    content = @Content(schema = @Schema(implementation = PrescriptionRequestDto.class))),
            @ApiResponse(responseCode = "204", description = "Aucune prescription trouvée pour cette consultation"),
            @ApiResponse(responseCode = "400", description = "ID de consultation invalide"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    List<PrescriptionRequestDto> findPrescriptionByConsultationId(
            @Parameter(description = "ID de la consultation", required = true, example = "1")
            @PathVariable("consultationId") Integer id);
}