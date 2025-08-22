package com.example.GestionClinique.controller;


import com.example.GestionClinique.dto.RequestDto.RendezVousRequestDto;
import com.example.GestionClinique.dto.ResponseDto.RendezVousResponseDto;
import com.example.GestionClinique.mapper.RendezVousMapper;
import com.example.GestionClinique.model.entity.RendezVous;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import com.example.GestionClinique.service.RendezVousService;
import com.example.GestionClinique.service.UtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.example.GestionClinique.configuration.utils.Constants.API_NAME;

@Tag(name = "Gestion des Rendez-vous", description = "API pour la gestion des rendez-vous médicaux")
@RequestMapping(API_NAME + "/rendezvous") // Changed to plural and hyphenated for REST convention
@RestController
public class RendezVousController {

    private final RendezVousService rendezVousService;
    private final RendezVousMapper rendezVousMapper;
    private final UtilisateurService utilisateurService;

    @Autowired
    public RendezVousController(RendezVousService rendezVousService, RendezVousMapper rendezVousMapper, UtilisateurService utilisateurService) {
        this.rendezVousService = rendezVousService;
        this.rendezVousMapper = rendezVousMapper;
        this.utilisateurService = utilisateurService;
    }



@PreAuthorize("hasAnyRole('SECRETAIRE')")
    @PostMapping(path = "/createRendezVous", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Créer un nouveau rendez-vous médical",
            description = "Permet de programmer un nouveau rendez-vous entre un patient et un professionnel de santé")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rendez-vous créé avec succès",
                    content = @Content(schema = @Schema(implementation = RendezVousResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Données du rendez-vous invalides ou conflit de planning"),
            @ApiResponse(responseCode = "404", description = "Patient ou professionnel de santé non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la création")
    })
    public ResponseEntity<RendezVousResponseDto> createRendezVous(
            @Parameter(description = "Détails du rendez-vous à créer", required = true)
            @Valid @RequestBody RendezVousRequestDto rendezVousRequestDto) {
            RendezVous rendezVousToCreate = rendezVousMapper.toEntity(rendezVousRequestDto);
            RendezVous createdRendezVous = rendezVousService.createRendezVous(rendezVousToCreate);
            RendezVousResponseDto responseDto = rendezVousMapper.toDto(createdRendezVous);
            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }



    @PreAuthorize("hasAnyRole('SECRETAIRE', 'MEDECIN', 'ADMIN')")
    @GetMapping(path = "/{idRendezVous}", produces = MediaType.APPLICATION_JSON_VALUE) // Simplified path
    @Operation(summary = "Obtenir un rendez-vous par son ID",
            description = "Récupère les informations détaillées d'un rendez-vous spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rendez-vous trouvé et retourné",
                    content = @Content(schema = @Schema(implementation = RendezVousResponseDto.class))), // ResponseDto
            @ApiResponse(responseCode = "400", description = "Format d'ID de rendez-vous invalide"),
            @ApiResponse(responseCode = "404", description = "Rendez-vous non trouvé avec l'ID fourni"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    public ResponseEntity<RendezVousResponseDto> findRendezVousById(
            @Parameter(description = "ID du rendez-vous à récupérer", required = true, example = "123")
            @PathVariable("idRendezVous") Long id) {
            RendezVous rendezVous = rendezVousService.findRendezVousById(id);
            return ResponseEntity.ok(rendezVousMapper.toDto(rendezVous));
    }



@PreAuthorize("hasAnyRole('SECRETAIRE')")
    @PutMapping(path = "/{idRendezVous}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE) // Simplified path
    @Operation(summary = "Mettre à jour un rendez-vous existant",
            description = "Modifie les détails d'un rendez-vous programmé")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rendez-vous mis à jour avec succès",
                    content = @Content(schema = @Schema(implementation = RendezVousResponseDto.class))), // ResponseDto
            @ApiResponse(responseCode = "400", description = "Données de mise à jour invalides ou conflit de planning"),
            @ApiResponse(responseCode = "404", description = "Rendez-vous non trouvé avec l'ID fourni"),
            @ApiResponse(responseCode = "409", description = "Conflit de planning (créneau déjà pris)"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la mise à jour")
    })
    public ResponseEntity<RendezVousResponseDto> updateRendezVous(
            @Parameter(description = "ID du rendez-vous à mettre à jour", required = true, example = "123")
            @PathVariable("idRendezVous") Long id,
            @Parameter(description = "Nouveaux détails du rendez-vous", required = true)
            @Valid @RequestBody RendezVousRequestDto rendezVousRequestDto) {
            RendezVous existingRendezVous = rendezVousService.findRendezVousById(id);
            rendezVousMapper.updateEntityFromDto(rendezVousRequestDto, existingRendezVous);
            RendezVous updatedRendezVous = rendezVousService.updateRendezVous(id, existingRendezVous);
            return ResponseEntity.ok(rendezVousMapper.toDto(updatedRendezVous));
    }



@PreAuthorize("hasAnyRole('SECRETAIRE')")
    @DeleteMapping(path = "/{idRendezVous}") // Simplified path
    @Operation(summary = "Supprimer un rendez-vous",
            description = "Annule et supprime définitivement un rendez-vous du système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Rendez-vous supprimé avec succès"),
            @ApiResponse(responseCode = "400", description = "Format d'ID de rendez-vous invalide"),
            @ApiResponse(responseCode = "404", description = "Rendez-vous non trouvé avec l'ID fourni"),
            @ApiResponse(responseCode = "403", description = "Impossible de supprimer (e.g., passé, lié à consultation)"), // Forbidden/Conflict
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la suppression")
    })
    public ResponseEntity<Void> deleteRendezVous(
            @Parameter(description = "ID du rendez-vous à supprimer", required = true, example = "123")
            @PathVariable("idRendezVous") Long id) {
            rendezVousService.deleteRendezVous(id);
            return ResponseEntity.noContent().build();
    }



@PreAuthorize("hasAnyRole('SECRETAIRE', 'MEDECIN', 'ADMIN')") // Adjust roles as needed
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE) // Simplified path for all rendezvous
    @Operation(summary = "Lister tous les rendez-vous",
            description = "Récupère la liste complète de tous les rendez-vous programmés")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste complète des rendez-vous retournée",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RendezVousResponseDto.class)))), // ArraySchema
            @ApiResponse(responseCode = "204", description = "Aucun rendez-vous trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    public ResponseEntity<List<RendezVousResponseDto>> findAllRendezVous() {
        List<RendezVous> rendezvousList = rendezVousService.findAllRendezVous();
        if (rendezvousList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(rendezVousMapper.toDtoList(rendezvousList));
    }



@PreAuthorize("hasAnyRole('SECRETAIRE', 'MEDECIN')")
    @GetMapping(path = "/statut/{statut}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher des rendez-vous par statut",
            description = "Filtre les rendez-vous selon leur statut (confirmé, annulé, etc.)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rendez-vous filtrés retournés avec succès",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RendezVousResponseDto.class)))),
            @ApiResponse(responseCode = "204", description = "Aucun rendez-vous trouvé avec ce statut"),
            @ApiResponse(responseCode = "400", description = "Valeur de statut invalide"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors du filtrage")
    })
    public ResponseEntity<List<RendezVousResponseDto>> findRendezVousByStatut(
            @Parameter(description = "Statut pour filtrer", required = true, schema = @Schema(implementation = StatutRDV.class))
            @PathVariable("statut") StatutRDV statut) {
        List<RendezVous> rendezvousList = rendezVousService.findRendezVousByStatut(statut);
        if (rendezvousList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(rendezVousMapper.toDtoList(rendezvousList));
    }



@PreAuthorize("hasAnyRole('SECRETAIRE', 'MEDECIN')")
    @GetMapping(path = "/salle/{idSalle}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher des rendez-vous par salle",
            description = "Liste tous les rendez-vous programmés dans une salle spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rendez-vous par salle retournés avec succès",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RendezVousResponseDto.class)))),
            @ApiResponse(responseCode = "204", description = "Aucun rendez-vous trouvé pour cette salle"),
            @ApiResponse(responseCode = "400", description = "Format d'ID de salle invalide"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la recherche")
    })
    public ResponseEntity<List<RendezVousResponseDto>> findRendezVousBySalleId(
            @Parameter(description = "ID de la salle à rechercher", required = true, example = "5")
            @PathVariable("idSalle") Long id) {
        List<RendezVous> rendezvousList = rendezVousService.findRendezVousBySalleId(id);
        if (rendezvousList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(rendezVousMapper.toDtoList(rendezvousList));
    }



    // The 'isRendezVousAvailable' endpoint signature needs to change to pass IDs, not full objects
@PreAuthorize("hasAnyRole('SECRETAIRE', 'MEDECIN')")
    @GetMapping(path = "/available", produces = MediaType.APPLICATION_JSON_VALUE) // Changed path for a query endpoint
    @Operation(summary = "Vérifier la disponibilité d'un créneau",
            description = "Vérifie si un créneau horaire est disponible pour un médecin et une salle spécifiques.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disponibilité vérifiée avec succès (true/false)",
                    content = @Content(schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "400", description = "Paramètres de requête manquants ou format invalide."),
            @ApiResponse(responseCode = "404", description = "Médecin ou salle non trouvé."),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur.")
    })
    public ResponseEntity<Boolean> isRendezVousAvailable(
            @Parameter(description = "Date du rendez-vous (yyyy-MM-dd)", required = true, example = "2025-06-28")
            @RequestParam("jour") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate jour,
            @Parameter(description = "Heure du rendez-vous (HH:mm:ss)", required = true, example = "14:30:00")
            @RequestParam("heure") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime heure,
            @Parameter(description = "ID du médecin", required = true, example = "3")
            @RequestParam("medecinId") Long medecinId,
            @Parameter(description = "ID de la salle", required = true, example = "2")
            @RequestParam("salleId") Long salleId) {

            boolean available = rendezVousService.isRendezVousAvailable(jour, heure, medecinId, salleId);
            return ResponseEntity.ok(available);

    }



@PreAuthorize("hasAnyRole('SECRETAIRE', 'MEDECIN')") // Patient can view their own
    @GetMapping(path = "/jour/{jour}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher des rendez-vous par jour",
            description = "Récupère une liste de tous les rendez-vous programmés pour une date spécifique.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des rendez-vous pour le jour retournée avec succès",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = RendezVousResponseDto.class)))),
            @ApiResponse(responseCode = "204", description = "Aucun rendez-vous trouvé pour ce jour"),
            @ApiResponse(responseCode = "400", description = "Format de date invalide"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la recherche")
    })
    public ResponseEntity<List<RendezVousResponseDto>> findRendezVousByJour(
            @Parameter(description = "Date du jour à rechercher (format yyyy-MM-dd)", required = true, example = "2025-06-28")
            @PathVariable("jour") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate jour) { // Use @DateTimeFormat
        List<RendezVous> rendezvousList = rendezVousService.findRendezVousByJour(jour);
        if (rendezvousList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(rendezVousMapper.toDtoList(rendezvousList));
    }



@PreAuthorize("hasAnyRole('SECRETAIRE')")
    @PutMapping(path = "/{idRendezVous}/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Annuler un rendez-vous",
            description = "Change le statut d'un rendez-vous existant à 'annulé'")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rendez-vous annulé avec succès",
                    content = @Content(schema = @Schema(implementation = RendezVousResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Format d'ID de rendez-vous invalide ou RV déjà annulé/passé"),
            @ApiResponse(responseCode = "404", description = "Rendez-vous non trouvé avec l'ID fourni"),
            @ApiResponse(responseCode = "409", description = "Conflit: impossible d'annuler (déjà terminé/annulé)"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de l'annulation")
    })
    public ResponseEntity<RendezVousResponseDto> cancelRendezVous(
            @Parameter(description = "ID du rendez-vous à annuler", required = true, example = "123")
            @PathVariable("idRendezVous") Long idRendezVous) {
            RendezVous canceledRendezVous = rendezVousService.cancelRendezVous(idRendezVous);
            return ResponseEntity.ok(rendezVousMapper.toDto(canceledRendezVous));

    }


    @PostMapping("/cancel-old")
    @Operation(summary = "Annuler un rendez-vous et supprimer facture liée",
            description = "annuler un vieux rendezVous et supprimer la facture liée")
    public ResponseEntity<String> cancelOldRendezVous() {
        rendezVousService.cancelRendezVousByJour();
        return ResponseEntity.ok("Tous les rendez-vous antérieurs à la date d'aujourd'hui ont été annulés.");
    }


    @PreAuthorize("hasAnyRole('SECRETAIRE', 'ADMIN', 'MEDECIN')")
    @GetMapping("/utilisateurs/{idUtilisateur}/confirmed/month/{year}/{month}")
    @Operation(summary = "Obtenir les rendez-vous confirmés d'un utilisateur pour un mois donné",
            description = "Récupère tous les rendez-vous confirmés pour un utilisateur spécifique dans un mois et une année donnés.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Liste des rendez-vous confirmés",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = RendezVousResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
                    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
            })
    public ResponseEntity<List<RendezVousResponseDto>> getConfirmedRendezVousByMonth(
            @PathVariable @Parameter(description = "ID de l'utilisateur") Long idUtilisateur,
            @PathVariable @Parameter(description = "Année des rendez-vous") int year,
            @PathVariable @Parameter(description = "Mois des rendez-vous (1-12)") int month) {

        List<RendezVous> rendezVousEntities = rendezVousService.findUtilisateurConfirmedRendezVousByMonth(idUtilisateur, year, month);

        List<RendezVousResponseDto> rendezVousDtos = rendezVousMapper.toDtoList(rendezVousEntities);

        return ResponseEntity.ok(rendezVousDtos);
    }



    @PreAuthorize("hasAnyRole('SECRETAIRE', 'ADMIN', 'MEDECIN')")
    @GetMapping("/month/{year}/{month}")
    @Operation(summary = "Obtenir les rendez-vous pour un mois donné",
            description = "Récupère tous les rendez-vous dans un mois et une année donnés.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Liste des rendez-vous ",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = RendezVousResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "rendezVous non trouvé"),
                    @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
            })
    public ResponseEntity<List<RendezVousResponseDto>> getRendezVousByMonth(
            @PathVariable @Parameter(description = "Année des rendez-vous") int year,
            @PathVariable @Parameter(description = "Mois des rendez-vous (1-12)") int month) {

        List<RendezVous> rendezVousEntities = rendezVousService.findRendezVousByMonth(year, month);

        List<RendezVousResponseDto> rendezVousDtos = rendezVousMapper.toDtoList(rendezVousEntities);

        return ResponseEntity.ok(rendezVousDtos);
    }
}
