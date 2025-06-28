package com.example.GestionClinique.controller.controllerApi;

import com.example.GestionClinique.dto.RequestDto.FactureRequestDto;
import com.example.GestionClinique.dto.RequestDto.PatientRequestDto;
import com.example.GestionClinique.model.entity.enumElem.ModePaiement;
import com.example.GestionClinique.model.entity.enumElem.StatutPaiement;
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

import static com.example.GestionClinique.utils.Constants.API_NAME;

@Tag(name = "Gestion des Factures", description = "API pour la gestion des factures et des paiements")
@RequestMapping(API_NAME + "/factures")
public interface FactureApi {

    @PreAuthorize("hasAnyRole('SECRETAIRE')")
    @PostMapping(path = "/create/{consultationId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Créer une facture pour une consultation",
            description = "Crée une nouvelle facture associée à une consultation existante avec les détails de paiement")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Facture créée avec succès",
                    content = @Content(schema = @Schema(implementation = FactureRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "Données de la facture invalides ou incomplètes"),
            @ApiResponse(responseCode = "404", description = "Consultation non trouvée avec l'ID spécifié"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la création")
    })
    FactureRequestDto createFactureForConsultation(
            @Parameter(description = "ID de la consultation à facturer", required = true, example = "1")
            @PathVariable("consultationId") Integer consultationId,
            @Parameter(description = "Détails de la facture à créer", required = true,
                    content = @Content(schema = @Schema(implementation = FactureRequestDto.class)))
            @RequestBody FactureRequestDto factureRequestDto);




    @PreAuthorize("hasAnyRole('SECRETAIRE')")
    @PutMapping(path = "/update/{idFacture}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour une facture",
            description = "Met à jour tous les détails d'une facture existante (montant, description, etc.)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Facture mise à jour avec succès",
                    content = @Content(schema = @Schema(implementation = FactureRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "Données de la facture invalides ou incomplètes"),
            @ApiResponse(responseCode = "404", description = "Facture non trouvée avec l'ID spécifié"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la mise à jour")
    })
    FactureRequestDto updateFacture(
            @Parameter(description = "ID de la facture à mettre à jour", required = true, example = "1")
            @PathVariable("idFacture") Integer id,
            @Parameter(description = "Nouveaux détails de la facture", required = true,
                    content = @Content(schema = @Schema(implementation = FactureRequestDto.class)))
            @RequestBody FactureRequestDto factureRequestDto);




    @PreAuthorize("hasAnyRole('SECRETAIRE')")
    @GetMapping(path = "/recherche/allFacture", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Lister toutes les factures",
            description = "Récupère la liste complète des factures avec leurs détails")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des factures récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = FactureRequestDto.class))),
            @ApiResponse(responseCode = "204", description = "Aucune facture trouvée - liste vide"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    List<FactureRequestDto> findAllFactures();




    @PreAuthorize("hasAnyRole('SECRETAIRE')")
    @GetMapping(path = "/statut/{statutPaiement}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Filtrer les factures par statut de paiement",
            description = "Récupère les factures selon leur statut de paiement (PAYE, IMPAYE, EN_RETARD, etc.)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Factures filtrées récupérées avec succès",
                    content = @Content(schema = @Schema(implementation = FactureRequestDto.class))),
            @ApiResponse(responseCode = "204", description = "Aucune facture trouvée pour ce statut"),
            @ApiResponse(responseCode = "400", description = "Statut de paiement invalide ou inconnu"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors du filtrage")
    })
    List<FactureRequestDto> findFacturesByStatut(
            @Parameter(description = "Statut de paiement pour le filtrage", required = true,
                    schema = @Schema(implementation = StatutPaiement.class), example = "PAYE")
            @PathVariable("statutPaiement") StatutPaiement statutPaiement);




    @PreAuthorize("hasAnyRole('SECRETAIRE')")
    @GetMapping(path = "/mode/{modePaiement}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Filtrer les factures par mode de paiement",
            description = "Récupère les factures selon leur mode de paiement (CARTE, ESPECES, VIREMENT, etc.)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Factures filtrées récupérées avec succès",
                    content = @Content(schema = @Schema(implementation = FactureRequestDto.class))),
            @ApiResponse(responseCode = "204", description = "Aucune facture trouvée pour ce mode"),
            @ApiResponse(responseCode = "400", description = "Mode de paiement invalide ou inconnu"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors du filtrage")
    })
    List<FactureRequestDto> findFacturesByModePaiement(
            @Parameter(description = "Mode de paiement pour le filtrage", required = true,
                    schema = @Schema(implementation = ModePaiement.class), example = "CARTE")
            @PathVariable("modePaiement") ModePaiement modePaiement);




    @PreAuthorize("hasAnyRole('SECRETAIRE')")
    @GetMapping(path = "/recherche/{idFacture}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir une facture par son ID",
            description = "Récupère tous les détails d'une facture spécifique, y compris les éléments facturés")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Facture trouvée et retournée",
                    content = @Content(schema = @Schema(implementation = FactureRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "ID de facture invalide ou mal formé"),
            @ApiResponse(responseCode = "404", description = "Facture non trouvée avec l'ID spécifié"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la recherche")
    })
    FactureRequestDto findById(
            @Parameter(description = "ID unique de la facture à récupérer", required = true, example = "1")
            @PathVariable("idFacture") Integer id);




    @PreAuthorize("hasAnyRole('SECRETAIRE')")
    @DeleteMapping(path = "/{idFacture}")
    @Operation(summary = "Supprimer une facture",
            description = "Supprime définitivement une facture du système (opération irréversible)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Facture supprimée avec succès - pas de contenu retourné"),
            @ApiResponse(responseCode = "400", description = "ID de facture invalide ou mal formé"),
            @ApiResponse(responseCode = "404", description = "Facture non trouvée avec l'ID spécifié"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la suppression")
    })
    void deleteFacture(
            @Parameter(description = "ID de la facture à supprimer", required = true, example = "1")
            @PathVariable("idFacture") Integer id);




    @PreAuthorize("hasAnyRole('SECRETAIRE')")
    @GetMapping(path = "/{idFacture}/patient", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir le patient associé à une facture",
            description = "Récupère les informations du patient lié à une facture spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Patient trouvé et retourné",
                    content = @Content(schema = @Schema(implementation = PatientRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "ID de facture invalide ou mal formé"),
            @ApiResponse(responseCode = "404", description = "Facture ou patient associé non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la recherche")
    })
    PatientRequestDto findPatientByFactureId(
            @Parameter(description = "ID de la facture pour trouver le patient associé", required = true, example = "1")
            @PathVariable("idFacture") Integer id);




    @PreAuthorize("hasAnyRole('SECRETAIRE')")
    @PatchMapping(path = "/{idFacture}/statut/{nouveauStatut}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour le statut de paiement",
            description = "Modifie uniquement le statut de paiement d'une facture existante (PAYE, IMPAYE, etc.)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statut mis à jour avec succès",
                    content = @Content(schema = @Schema(implementation = FactureRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "ID de facture ou statut invalide"),
            @ApiResponse(responseCode = "404", description = "Facture non trouvée avec l'ID spécifié"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la mise à jour")
    })
    FactureRequestDto updateStatutPaiement(
            @Parameter(description = "ID de la facture à mettre à jour", required = true, example = "1")
            @PathVariable("idFacture") Integer id,
            @Parameter(description = "Nouveau statut de paiement", required = true,
                    schema = @Schema(implementation = StatutPaiement.class), example = "PAYE")
            @PathVariable("nouveauStatut") StatutPaiement nouveauStatut);
}