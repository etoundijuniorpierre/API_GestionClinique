package com.example.GestionClinique.controller.controllerApi;

import com.example.GestionClinique.dto.RequestDto.RendezVousRequestDto;
import com.example.GestionClinique.model.entity.Salle;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
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
import java.time.LocalTime;
import java.util.List;

import static com.example.GestionClinique.utils.Constants.API_NAME;

@Tag(name = "Gestion des Rendez-vous", description = "API pour la gestion des rendez-vous médicaux")
@RequestMapping(API_NAME + "/rendezVous")
public interface RendezVousApi {

    @PreAuthorize("hasAnyRole('SECRETAIRE')")
    @PostMapping(path = "/createRendezVous", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Créer un nouveau rendez-vous médical",
            description = "Permet de programmer un nouveau rendez-vous entre un patient et un professionnel de santé")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rendez-vous créé avec succès",
                    content = @Content(schema = @Schema(implementation = RendezVousRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "Données du rendez-vous invalides ou conflit de planning"),
            @ApiResponse(responseCode = "404", description = "Patient ou professionnel de santé non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la création")
    })
    RendezVousRequestDto createRendezVous(
            @Parameter(description = "Détails du rendez-vous à créer", required = true,
                    content = @Content(schema = @Schema(implementation = RendezVousRequestDto.class)))
            @RequestBody RendezVousRequestDto rendezVousRequestDto);




    @PreAuthorize("hasAnyRole('SECRETAIRE')")
    @GetMapping(path = "/recherche/id/{idRendezVous}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir un rendez-vous par son ID",
            description = "Récupère les informations détaillées d'un rendez-vous spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rendez-vous trouvé et retourné",
                    content = @Content(schema = @Schema(implementation = RendezVousRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "Format d'ID de rendez-vous invalide"),
            @ApiResponse(responseCode = "404", description = "Rendez-vous non trouvé avec l'ID fourni"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    RendezVousRequestDto findRendezVousById(
            @Parameter(description = "ID du rendez-vous à récupérer", required = true,
                    example = "123")
            @PathVariable("idRendezVous") Integer id);






    @PreAuthorize("hasAnyRole('SECRETAIRE')")
    @PutMapping(path = "/update/{idRendezVous}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour un rendez-vous existant",
            description = "Modifie les détails d'un rendez-vous programmé")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rendez-vous mis à jour avec succès",
                    content = @Content(schema = @Schema(implementation = RendezVousRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "Données de mise à jour invalides ou conflit de planning"),
            @ApiResponse(responseCode = "404", description = "Rendez-vous non trouvé avec l'ID fourni"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la mise à jour")
    })
    RendezVousRequestDto updateRendezVous(
            @Parameter(description = "ID du rendez-vous à mettre à jour", required = true,
                    example = "123")
            @PathVariable("idRendezVous") Integer id,
            @Parameter(description = "Nouveaux détails du rendez-vous", required = true,
                    content = @Content(schema = @Schema(implementation = RendezVousRequestDto.class)))
            @RequestBody RendezVousRequestDto rendezVousRequestDto);







    @PreAuthorize("hasAnyRole('SECRETAIRE')")
    @DeleteMapping(path = "/delete/{idRendezVous}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Supprimer un rendez-vous",
            description = "Annule et supprime définitivement un rendez-vous du système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Rendez-vous supprimé avec succès"),
            @ApiResponse(responseCode = "400", description = "Format d'ID de rendez-vous invalide"),
            @ApiResponse(responseCode = "404", description = "Rendez-vous non trouvé avec l'ID fourni"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la suppression")
    })
    void deleteRendezVous(
            @Parameter(description = "ID du rendez-vous à supprimer", required = true,
                    example = "123")
            @PathVariable("idRendezVous") Integer id);






    @PreAuthorize("hasAnyRole('SECRETAIRE')")
    @GetMapping(path = "/recherche/allRendezVous", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Lister tous les rendez-vous",
            description = "Récupère la liste complète de tous les rendez-vous programmés")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste complète des rendez-vous retournée",
                    content = @Content(schema = @Schema(implementation = RendezVousRequestDto.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    List<RendezVousRequestDto> findAllRendezVous();





    @PreAuthorize("hasAnyRole('SECRETAIRE')")
    @GetMapping(path = "/statut/{statut}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher des rendez-vous par statut",
            description = "Filtre les rendez-vous selon leur statut (confirmé, annulé, etc.)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rendez-vous filtrés retournés avec succès",
                    content = @Content(schema = @Schema(implementation = RendezVousRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "Valeur de statut invalide"),
            @ApiResponse(responseCode = "404", description = "Aucun rendez-vous trouvé avec ce statut"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors du filtrage")
    })
    List<RendezVousRequestDto> findRendezVousByStatut(
            @Parameter(description = "Statut pour filtrer", required = true,
                    schema = @Schema(implementation = StatutRDV.class))
            @PathVariable("statut") StatutRDV statut);





    @PreAuthorize("hasAnyRole('SECRETAIRE')")
    @GetMapping(path = "/salle/{idSalle}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher des rendez-vous par salle",
            description = "Liste tous les rendez-vous programmés dans une salle spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rendez-vous par salle retournés avec succès",
                    content = @Content(schema = @Schema(implementation = RendezVousRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "Format d'ID de salle invalide"),
            @ApiResponse(responseCode = "404", description = "Aucun rendez-vous trouvé pour cette salle"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la recherche")
    })
    List<RendezVousRequestDto> findRendezVousBySalleId(
            @Parameter(description = "ID de la salle à rechercher", required = true,
                    example = "5")
            @PathVariable("idSalle") Integer id);





    @PreAuthorize("hasAnyRole('SECRETAIRE')")
    @GetMapping(path = "/patient/{idPatient}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher des rendez-vous par patient",
            description = "Liste tous les rendez-vous d'un patient spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rendez-vous du patient retournés avec succès",
                    content = @Content(schema = @Schema(implementation = RendezVousRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "Format d'ID patient invalide"),
            @ApiResponse(responseCode = "404", description = "Aucun rendez-vous trouvé pour ce patient"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la recherche")
    })
    List<RendezVousRequestDto> findRendezVousByPatientId(
            @Parameter(description = "ID du patient à rechercher", required = true,
                    example = "42")
            @PathVariable("idPatient") Integer id);





    @PreAuthorize("hasAnyRole('SECRETAIRE', 'MEDECIN')")
    @GetMapping(path = "/medecin/{idMedecin}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher des rendez-vous par professionnel de santé",
            description = "Liste tous les rendez-vous d'un médecin ou professionnel de santé spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rendez-vous du professionnel retournés avec succès",
                    content = @Content(schema = @Schema(implementation = RendezVousRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "Format d'ID professionnel invalide"),
            @ApiResponse(responseCode = "404", description = "Aucun rendez-vous trouvé pour ce professionnel"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la recherche")
    })
    List<RendezVousRequestDto> findRendezVousByMedecinId(
            @Parameter(description = "ID du professionnel de santé", required = true,
                    example = "7")
            @PathVariable("idMedecin") Integer id);




    @PreAuthorize("hasAnyRole('SECRETAIRE')")
    @GetMapping(path = "/disponibilite/{jour}/{heure}/{medecinId}/{salleId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Vérifier la disponibilité d'un créneau (TENTATIVE DE MATCHING DIRECT)",
            description = "Tente de vérifier la disponibilité d'un créneau horaire, mais cette signature est problématique pour REST.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Disponibilité vérifiée avec succès (si ça marche)",
                    content = @Content(schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "400", description = "**ATTENTION: Paramètres non supportés ou format invalide.**"),
            @ApiResponse(responseCode = "404", description = "Ressource non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    boolean isRendezVousAvailable(
            @Parameter(description = "Date du rendez-vous (yyyy-MM-dd)", required = true, example = "2023-12-31")
            @PathVariable("jour") LocalDate jour,
            @Parameter(description = "Heure du rendez-vous (HH:mm:ss)", required = true, example = "14:30:00")
            @PathVariable("heure") LocalTime heure,
            @Parameter(description = "ID du médecin", required = true, example = "3")
            @PathVariable("medecinId") Utilisateur medecin,
            @Parameter(description = "ID de la salle", required = true, example = "2")
            @PathVariable("salleId") Salle salle
    );




    @PreAuthorize("hasAnyRole('SECRETAIRE')")
    @GetMapping(path = "/jour/{jour}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher des rendez-vous par jour",
            description = "Récupère une liste de tous les rendez-vous programmés pour une date spécifique.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des rendez-vous pour le jour retournée avec succès",
                    content = @Content(schema = @Schema(implementation = RendezVousRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "Format de date invalide"),
            @ApiResponse(responseCode = "404", description = "Aucun rendez-vous trouvé pour ce jour"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la recherche")
    })
    List<RendezVousRequestDto> findRendezVousByJour(
            @Parameter(description = "Date du jour à rechercher (format yyyy-MM-dd)", required = true,
                    example = "2025-06-22") // Example is current date
            @PathVariable("jour") LocalDate jour);




    @PreAuthorize("hasAnyRole('SECRETAIRE')")
    @PutMapping(path = "/annuler/{idRendezVous}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Annuler un rendez-vous",
            description = "Change le statut d'un rendez-vous existant à 'annulé'")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rendez-vous annulé avec succès",
                    content = @Content(schema = @Schema(implementation = RendezVousRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "Format d'ID de rendez-vous invalide"),
            @ApiResponse(responseCode = "404", description = "Rendez-vous non trouvé avec l'ID fourni"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de l'annulation")
    })
    RendezVousRequestDto cancelRendezVous(
            @Parameter(description = "ID du rendez-vous à annuler", required = true,
                    example = "123")
            @PathVariable("idRendezVous") Integer idRendezVous);
}