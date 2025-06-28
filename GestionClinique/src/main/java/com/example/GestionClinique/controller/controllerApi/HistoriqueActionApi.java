package com.example.GestionClinique.controller.controllerApi;

import com.example.GestionClinique.dto.RequestDto.HistoriqueActionRequestDto;
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

import java.time.LocalDate;
import java.util.List;

import static com.example.GestionClinique.utils.constants.API_NAME;

@Tag(name = "Gestion des Historiques d'Actions", description = "API pour la gestion et le suivi des actions dans le système")
@RequestMapping(API_NAME + "/historiqueActions")
public interface HistoriqueActionApi {


//    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    @Operation(summary = "Enregistrer une action dans l'historique",
//            description = "Enregistre une nouvelle action dans l'historique des actions du système avec tous les détails nécessaires")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "201", description = "Action enregistrée avec succès",
//                    content = @Content(schema = @Schema(implementation = HistoriqueActionDto.class))),
//            @ApiResponse(responseCode = "400", description = "Données de l'action invalides ou incomplètes"),
//            @ApiResponse(responseCode = "404", description = "Ressource ou utilisateur associé non trouvé"),
//            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de l'enregistrement")
//    })
//    HistoriqueActionDto save(
//            @Parameter(description = "Détails de l'action historique à enregistrer", required = true,
//                    content = @Content(schema = @Schema(implementation = HistoriqueActionDto.class)))
//            @RequestBody HistoriqueActionDto historiqueActionDto);




    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(path = "/recherche/allHistorique", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Lister tout l'historique des actions",
            description = "Récupère la liste complète et chronologique de toutes les actions enregistrées dans le système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historique récupéré avec succès",
                    content = @Content(schema = @Schema(implementation = HistoriqueActionRequestDto.class))),
            @ApiResponse(responseCode = "204", description = "Aucune action trouvée - historique vide"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    List<HistoriqueActionRequestDto> findAll();





    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(path = "/recherche/{idHistorique}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Récupérer une action spécifique par ID",
            description = "Trouve et retourne les détails complets d'une action particulière dans l'historique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Action trouvée et retournée",
                    content = @Content(schema = @Schema(implementation = HistoriqueActionRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "ID historique invalide ou mal formé"),
            @ApiResponse(responseCode = "404", description = "Action non trouvée avec l'ID spécifié"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la recherche")
    })
    HistoriqueActionRequestDto findById(
            @Parameter(description = "ID unique de l'action historique à récupérer", required = true, example = "1")
            @PathVariable("idHistorique") Integer id);




    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(path = "/utilisateur/{utilisateurId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Historique des actions par utilisateur",
            description = "Récupère la liste chronologique de toutes les actions effectuées par un utilisateur spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historique utilisateur récupéré avec succès",
                    content = @Content(schema = @Schema(implementation = HistoriqueActionRequestDto.class))),
            @ApiResponse(responseCode = "204", description = "Aucune action trouvée pour cet utilisateur"),
            @ApiResponse(responseCode = "400", description = "ID utilisateur invalide ou mal formé"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé avec l'ID spécifié"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    List<HistoriqueActionRequestDto> findByUtilisateurId(
            @Parameter(description = "ID de l'utilisateur dont on veut l'historique", required = true, example = "5")
            @PathVariable("utilisateurId") Integer id);




    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(path = "/periode/{debut}/{fin}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Filtrer l'historique par période temporelle",
            description = "Récupère toutes les actions enregistrées entre deux dates/heures spécifiques (inclusives)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historique filtré récupéré avec succès",
                    content = @Content(schema = @Schema(implementation = HistoriqueActionRequestDto.class))),
            @ApiResponse(responseCode = "204", description = "Aucune action trouvée dans la période spécifiée"),
            @ApiResponse(responseCode = "400", description = "Dates invalides ou période mal formée"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors du filtrage")
    })
    List<HistoriqueActionRequestDto> findByDateAfterAndDateBefore(
            @Parameter(description = "Date/heure de début de la période (format: YYYY-MM-DD)", required = true, example = "2023-01-01")
            @PathVariable("debut") LocalDate startDate,
            @Parameter(description = "Date/heure de fin de la période (format: YYYY-MM-DD)", required = true, example = "2023-12-31")
            @PathVariable("fin") LocalDate endDate);
}