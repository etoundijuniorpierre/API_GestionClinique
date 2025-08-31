package com.example.GestionClinique.controller;

import com.example.GestionClinique.dto.ResponseDto.HistoriqueActionResponseDto;
import com.example.GestionClinique.mapper.HistoriqueActionMapper;
import com.example.GestionClinique.model.entity.HistoriqueAction;
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

import com.example.GestionClinique.dto.RequestDto.HistoriqueActionRequestDto;
import com.example.GestionClinique.service.HistoriqueActionService;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

import static com.example.GestionClinique.configuration.utils.Constants.API_NAME;

@Tag(name = "Gestion des Historiques d'Actions", description = "API pour la gestion et le suivi des actions dans le système")
@RequestMapping(API_NAME + "/historiqueActions")
@RestController
public class HistoriqueActionController {

    private final HistoriqueActionService historiqueActionService;
    private final HistoriqueActionMapper historiqueActionMapper; // Inject mapper

    @Autowired
    public HistoriqueActionController(HistoriqueActionService historiqueActionService,
                                      HistoriqueActionMapper historiqueActionMapper) {
        this.historiqueActionService = historiqueActionService;
        this.historiqueActionMapper = historiqueActionMapper;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Enregistrer une action manuellement",
            description = "Permet d'enregistrer une action dans l'historique avec un utilisateur et une date spécifiés. Utilisation typique pour les audits ou les intégrations.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Action enregistrée avec succès",
                    content = @Content(schema = @Schema(implementation = HistoriqueActionResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Données fournies invalides ou incomplètes"),
            @ApiResponse(responseCode = "404", description = "Utilisateur associé non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<HistoriqueActionResponseDto> enregistrerActionManually(
            @Parameter(description = "DTO de l'historique d'action à créer", required = true,
                    content = @Content(schema = @Schema(implementation = HistoriqueActionRequestDto.class)))
            @Valid @RequestBody HistoriqueActionRequestDto historiqueActionRequestDto) {

            // Convert DTO to entity. Service will fetch the Utilisateur.
            HistoriqueAction actionToSave = historiqueActionMapper.toEntity(historiqueActionRequestDto);
            // Call the appropriate service method to save the action with the user ID
            HistoriqueAction savedAction = historiqueActionService.enregistrerAction(
                    actionToSave.getAction(),
                    historiqueActionRequestDto.getUtilisateurId() // Pass ID from DTO
            );
            return new ResponseEntity<>(historiqueActionMapper.toDto(savedAction), HttpStatus.CREATED);
    }

@PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE) // Removed "/recherche/allHistorique" for root GET
    @Operation(summary = "Lister tout l'historique des actions",
            description = "Récupère la liste complète et chronologique de toutes les actions enregistrées dans le système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historique récupéré avec succès",
                    content = @Content(schema = @Schema(implementation = HistoriqueActionResponseDto.class))),
            @ApiResponse(responseCode = "204", description = "Aucune action trouvée - historique vide"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    public ResponseEntity<List<HistoriqueActionResponseDto>> findAllHistoriqueActions() {
        List<HistoriqueAction> actions = historiqueActionService.findAllHistoriqueActionsDesc();
        if (actions.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(historiqueActionMapper.toDtoList(actions)); // Map to Response DTO list
    }

@PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(path = "/{idHistorique}", produces = MediaType.APPLICATION_JSON_VALUE) // Removed "/recherche"
    @Operation(summary = "Récupérer une action spécifique par ID",
            description = "Trouve et retourne les détails complets d'une action particulière dans l'historique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Action trouvée et retournée",
                    content = @Content(schema = @Schema(implementation = HistoriqueActionResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Action non trouvée avec l'ID spécifié"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la recherche")
    })
    public ResponseEntity<HistoriqueActionResponseDto> findHistoriqueActionById(
            @Parameter(description = "ID unique de l'action historique à récupérer", required = true, example = "1")
            @PathVariable("idHistorique") Long id) {

            HistoriqueAction action = historiqueActionService.findHistoriqueActionById(id);
            return ResponseEntity.ok(historiqueActionMapper.toDto(action));
    }

@PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(path = "/utilisateur/{utilisateurId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Historique des actions par utilisateur",
            description = "Récupère la liste chronologique de toutes les actions effectuées par un utilisateur spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historique utilisateur récupéré avec succès",
                    content = @Content(schema = @Schema(implementation = HistoriqueActionResponseDto.class))),
            @ApiResponse(responseCode = "204", description = "Aucune action trouvée pour cet utilisateur"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé avec l'ID spécifié"), // Added 404 for user not found
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    public ResponseEntity<List<HistoriqueActionResponseDto>> findHistoriqueActionsByUtilisateurId(
            @Parameter(description = "ID de l'utilisateur dont on veut l'historique", required = true, example = "5")
            @PathVariable("utilisateurId") Long utilisateurId) { // Changed to Long

            List<HistoriqueAction> actions = historiqueActionService.findHistoriqueActionsByUtilisateurId(utilisateurId);
            if (actions.isEmpty()) {
                return ResponseEntity.noContent().build(); // 204 No Content
            }
            return ResponseEntity.ok(historiqueActionMapper.toDtoList(actions));
    }

@PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(path = "/utilisateur/nom/{utilisateurName}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Historique des actions par nom d'utilisateur",
            description = "Récupère la liste chronologique de toutes les actions effectuées par un utilisateur dont le nom contient la chaîne spécifiée.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historique utilisateur récupéré avec succès",
                    content = @Content(schema = @Schema(implementation = HistoriqueActionResponseDto.class))),
            @ApiResponse(responseCode = "204", description = "Aucune action trouvée pour ce nom d'utilisateur"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    public ResponseEntity<List<HistoriqueActionResponseDto>> findHistoriqueActionsByUtilisateurName(
            @Parameter(description = "Nom (partiel) de l'utilisateur", required = true, example = "Jean")
            @PathVariable("utilisateurName") String utilisateurName) {

            List<HistoriqueAction> actions = historiqueActionService.findHistoriqueActionsByUtilisateurName(utilisateurName);
            if (actions.isEmpty()) {
                return ResponseEntity.noContent().build(); // 204 No Content
            }
            return ResponseEntity.ok(historiqueActionMapper.toDtoList(actions));
    }

@PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(path = "/periode", produces = MediaType.APPLICATION_JSON_VALUE) // Use request parameters for date range
    @Operation(summary = "Filtrer l'historique par période temporelle",
            description = "Récupère toutes les actions enregistrées entre deux dates spécifiées (inclusives).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historique filtré récupéré avec succès",
                    content = @Content(schema = @Schema(implementation = HistoriqueActionResponseDto.class))),
            @ApiResponse(responseCode = "204", description = "Aucune action trouvée dans la période spécifiée"),
            @ApiResponse(responseCode = "400", description = "Dates invalides ou période mal formée"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors du filtrage")
    })
    public ResponseEntity<List<HistoriqueActionResponseDto>> findHistoriqueActionsByDateRange(
            @Parameter(description = "Date de début de la période (format: YYYY-MM-DD)", required = true, example = "2023-01-01")
            @RequestParam("startDate") LocalDate startDate, // Use @RequestParam
            @Parameter(description = "Date de fin de la période (format: YYYY-MM-DD)", required = true, example = "2023-12-31")
            @RequestParam("endDate") LocalDate endDate) { // Use @RequestParam
 
            List<HistoriqueAction> actions = historiqueActionService.findHistoriqueActionsByDateRange(startDate, endDate);
            if (actions.isEmpty()) {
                return ResponseEntity.noContent().build(); // 204 No Content
            }
            return ResponseEntity.ok(historiqueActionMapper.toDtoList(actions));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(path = "/recherche", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher dans l'historique des actions",
            description = "Filtre les actions par nom, prénom, email ou mot clé dans la description.")
    public ResponseEntity<List<HistoriqueActionResponseDto>> rechercherHistorique(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String prenom,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String motCle) {

        List<HistoriqueAction> resultats = historiqueActionService.rechercherHistorique(nom, prenom, email, motCle);

        if(resultats.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(historiqueActionMapper.toDtoList(resultats));
    }

}
