package com.example.GestionClinique.controller.controllerApi;

import com.example.GestionClinique.dto.RequestDto.SalleResquestDto;
import com.example.GestionClinique.model.entity.enumElem.StatutSalle;
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

import java.time.LocalDateTime;
import java.util.List;

import static com.example.GestionClinique.utils.constants.API_NAME;

@Tag(name = "Gestion des Salles", description = "API pour la gestion des salles d'hôpital")
@RequestMapping(API_NAME + "/salle")
public interface SalleApi {

    @PreAuthorize("hasAnyRole('SECRETAIRE', 'ADMIN')")
    @PostMapping(path = "/createSalle", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Créer une nouvelle salle",
            description = "Crée une nouvelle salle dans le système avec les détails fournis")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Salle créée avec succès",
                    content = @Content(schema = @Schema(implementation = SalleResquestDto.class))),
            @ApiResponse(responseCode = "400", description = "Données de la salle invalides"),
            @ApiResponse(responseCode = "404", description = "Ressource requise non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la création")
    })
    SalleResquestDto createSalle(
            @Parameter(description = "Détails de la salle à créer", required = true)
            @RequestBody SalleResquestDto salleResquestDto);




    @PreAuthorize("hasAnyRole('SECRETAIRE')")
    @GetMapping(path = "/recherche/{idSalle}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir une salle par ID",
            description = "Récupère les informations détaillées d'une salle spécifique par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Salle trouvée et retournée",
                    content = @Content(schema = @Schema(implementation = SalleResquestDto.class))),
            @ApiResponse(responseCode = "400", description = "Format d'ID de salle invalide"),
            @ApiResponse(responseCode = "404", description = "Salle non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la récupération")
    })
    SalleResquestDto findSalleById(
            @Parameter(description = "ID de la salle à récupérer", required = true)
            @PathVariable("idSalle") Integer id);




    @PreAuthorize("hasAnyRole('SECRETAIRE')")
    @GetMapping(path = "/byStatut/{statutSalle}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Trouver les salles par statut",
            description = "Récupère toutes les salles correspondant au statut spécifié")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des salles retournée avec succès",
                    content = @Content(schema = @Schema(implementation = SalleResquestDto.class))),
            @ApiResponse(responseCode = "400", description = "Valeur de statut invalide"),
            @ApiResponse(responseCode = "404", description = "Aucune salle trouvée avec ce statut"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la recherche")
    })
    List<SalleResquestDto> findSalleByStatut(
            @Parameter(description = "Statut pour filtrer les salles", required = true)
            @PathVariable("statutSalle") StatutSalle statutSalle);




    @PreAuthorize("hasAnyRole('SECRETAIRE')")
    @GetMapping(path = "/recherche/allSalle", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Lister toutes les salles",
            description = "Récupère une liste complète de toutes les salles du système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste complète des salles retournée",
                    content = @Content(schema = @Schema(implementation = SalleResquestDto.class))),
            @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la récupération")
    })
    List<SalleResquestDto> findAllSalle();




    @PreAuthorize("hasAnyRole('SECRETAIRE')")
    @PutMapping(path = "/update/{idSalle}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour une salle",
            description = "Met à jour les informations d'une salle existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Salle mise à jour avec succès",
                    content = @Content(schema = @Schema(implementation = SalleResquestDto.class))),
            @ApiResponse(responseCode = "400", description = "Données de salle invalides"),
            @ApiResponse(responseCode = "404", description = "Salle non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la mise à jour")
    })
    SalleResquestDto updateSalle(
            @Parameter(description = "ID de la salle à mettre à jour", required = true)
            @PathVariable("idSalle") Integer id,
            @Parameter(description = "Nouveaux détails de la salle", required = true)
            @RequestBody SalleResquestDto salleResquestDto);




    @PreAuthorize("hasAnyRole('SECRETAIRE')")
    @DeleteMapping(path = "/delete/{idSalle}")
    @Operation(summary = "Supprimer une salle",
            description = "Supprime définitivement une salle du système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Salle supprimée avec succès"),
            @ApiResponse(responseCode = "400", description = "Format d'ID de salle invalide"),
            @ApiResponse(responseCode = "404", description = "Salle non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la suppression")
    })
    void deleteSalle(
            @Parameter(description = "ID de la salle à supprimer", required = true)
            @PathVariable("idSalle") Integer id);




    @PreAuthorize("hasAnyRole('SECRETAIRE')")
    @GetMapping(path = "/salleDisponible/{dateHeureDebut}/{dureeMinutes}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Trouver les salles disponibles",
            description = "Récupère une liste des salles disponibles pour un créneau horaire spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des salles disponibles retournée",
                    content = @Content(schema = @Schema(implementation = SalleResquestDto.class))),
            @ApiResponse(responseCode = "400", description = "Paramètres de date/heure invalides"),
            @ApiResponse(responseCode = "404", description = "Aucune salle disponible trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la recherche")
    })
    List<SalleResquestDto> findAvailableSalles(
            @Parameter(description = "Date et heure de début du créneau désiré", required = true,
                    example = "2023-12-31T10:00:00")
            @PathVariable("dateHeureDebut") LocalDateTime dateHeureDebut,
            @Parameter(description = "Durée du créneau désiré en minutes", required = true,
                    example = "60")
            @PathVariable("dureeMinutes") Integer dureeMinutes);
}