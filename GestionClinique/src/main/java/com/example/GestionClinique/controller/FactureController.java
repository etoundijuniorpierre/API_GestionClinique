package com.example.GestionClinique.controller;

import com.example.GestionClinique.dto.RequestDto.FactureRequestDto;
import com.example.GestionClinique.dto.ResponseDto.FactureResponseDto; // Use FactureResponseDto
import com.example.GestionClinique.dto.ResponseDto.PatientResponseDto; // Use PatientResponseDto
import com.example.GestionClinique.mapper.FactureMapper; // Import mapper
import com.example.GestionClinique.mapper.PatientMapper; // Import Patient mapper
import com.example.GestionClinique.model.entity.Facture;
import com.example.GestionClinique.model.entity.Patient;
import com.example.GestionClinique.model.entity.enumElem.ModePaiement;
import com.example.GestionClinique.model.entity.enumElem.StatutPaiement;
import com.example.GestionClinique.service.FactureService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; // For @Valid annotation
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.GestionClinique.configuration.utils.Constants.API_NAME;

@Tag(name = "Gestion des Factures", description = "API pour la gestion des factures et des paiements")
@RequestMapping(API_NAME + "/factures")
@RestController
public class FactureController {

    private final FactureService factureService;
    private final FactureMapper factureMapper;
    private final PatientMapper patientMapper;

    @Autowired
    public FactureController(FactureService factureService, FactureMapper factureMapper, PatientMapper patientMapper) {
        this.factureService = factureService;
        this.factureMapper = factureMapper;
        this.patientMapper = patientMapper;
    }

//
//@PreAuthorize("hasAnyRole('SECRETAIRE')")
//    @PostMapping(path = "/generate-for-consultation/{consultationId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    @Operation(summary = "Générer une facture pour une consultation",
//            description = "Génère une nouvelle facture automatiquement associée à une consultation existante. Le montant est calculé à partir du service médical du médecin.")
//    @ApiResponses(value = {
//            @ApiResponse(description = "Facture générée avec succès",
//                    content = @Content(schema = @Schema(implementation = FactureResponseDto.class))),
//            @ApiResponse(description = "Données invalides ou incomplètes (ex: mode de paiement manquant)"),
//            @ApiResponse(description = "Consultation non trouvée avec l'ID spécifié"),
//            @ApiResponse(description = "Conflit: Une facture existe déjà pour cette consultation."),
//            @ApiResponse(description = "Erreur interne du serveur lors de la génération")
//    })
//    public ResponseEntity<FactureResponseDto> generateFactureForConsultation(
//            @Parameter(description = "ID de la consultation pour laquelle générer la facture", required = true, example = "1")
//            @PathVariable("consultationId") Long consultationId) {
//        Facture generatedFacture = factureService.generateInvoiceForRendesVous(consultationId);
//        return new ResponseEntity<>(factureMapper.toDto(generatedFacture), HttpStatus.CREATED);
//    }
//
//
//    @PreAuthorize("hasAnyRole('SECRETAIRE')")
//    @PostMapping(path = "/generate-for-rendezVous/{rendezVousId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
//    @Operation(summary = "Générer une facture pour un rendezVous",
//            description = "Génère une nouvelle facture automatiquement associée à une consultation existante. Le montant est calculé à partir du service médical du médecin.")
//    @ApiResponses(value = {
//            @ApiResponse(description = "Facture générée avec succès",
//                    content = @Content(schema = @Schema(implementation = FactureResponseDto.class))),
//            @ApiResponse(description = "Données invalides ou incomplètes (ex: mode de paiement manquant)"),
//            @ApiResponse(description = "rendezVous non trouvée avec l'ID spécifié"),
//            @ApiResponse(description = "Conflit: Une facture existe déjà pour ce RendezVous."),
//            @ApiResponse(description = "Erreur interne du serveur lors de la génération")
//    })
//    public ResponseEntity<FactureResponseDto> generateInvoiceForRendesVous(
//            @Parameter(description = "ID du rendezVous pour laquelle générer la facture", required = true, example = "1")
//            @PathVariable("rendezVousId") Long rendezVousId) {
//        Facture generatedFacture = factureService.generateInvoiceForRendesVous(rendezVousId);
//        return new ResponseEntity<>(factureMapper.toDto(generatedFacture), HttpStatus.CREATED);
//    }


@PreAuthorize("hasAnyRole('SECRETAIRE')")
    @PutMapping(path = "/update/{idFacture}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour une facture",
            description = "Met à jour tous les détails modifiables d'une facture existante (montant, date d'émission, etc.).")
    @ApiResponses(value = {
            @ApiResponse(description = "Facture mise à jour avec succès",
                    content = @Content(schema = @Schema(implementation = FactureResponseDto.class))),
            @ApiResponse(description = "Données de la facture invalides ou incomplètes"),
            @ApiResponse(description = "Facture non trouvée avec l'ID spécifié"),
            @ApiResponse(description = "Erreur interne du serveur lors de la mise à jour")
    })
    public ResponseEntity<FactureResponseDto> updateFacture(
            @Parameter(description = "ID de la facture à mettre à jour", required = true, example = "1")
            @PathVariable("idFacture") Long id,
            @Parameter(description = "Nouveaux détails de la facture", required = true,
                    content = @Content(schema = @Schema(implementation = FactureRequestDto.class)))
            @Valid @RequestBody FactureRequestDto factureRequestDto) {

        Facture existingFacture = factureService.findById(id);
        factureMapper.updateEntityFromDto(factureRequestDto, existingFacture);
        Facture updatedFacture = factureService.updateFacture(id, existingFacture);
        return ResponseEntity.ok(factureMapper.toDto(updatedFacture));
    }



@PreAuthorize("hasAnyRole('SECRETAIRE')")
    @GetMapping(path = "/recherche/allFacture", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Lister toutes les factures",
            description = "Récupère la liste complète des factures avec leurs détails.")
    @ApiResponses(value = {
            @ApiResponse(description = "Liste des factures récupérée avec succès",
                    content = @Content(schema = @Schema(implementation = FactureResponseDto.class))),
            @ApiResponse(description = "Aucune facture trouvée - liste vide"),
            @ApiResponse(description = "Erreur interne du serveur lors de la récupération")
    })
    public ResponseEntity<List<FactureResponseDto>> findAllFactures() {
        List<Facture> factures = factureService.findAllFactures();
        if (factures.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(factureMapper.toDtoList(factures));
    }



@PreAuthorize("hasAnyRole('SECRETAIRE')")
    @GetMapping(path = "/statut/{statutPaiement}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Filtrer les factures par statut de paiement",
            description = "Récupère les factures selon leur statut de paiement (PAYE, IMPAYE, EN_RETARD, etc.).")
    @ApiResponses(value = {
            @ApiResponse(description = "Factures filtrées récupérées avec succès",
                    content = @Content(schema = @Schema(implementation = FactureResponseDto.class))),
            @ApiResponse(description = "Aucune facture trouvée pour ce statut"),
            @ApiResponse(description = "Statut de paiement invalide ou inconnu"),
            @ApiResponse(description = "Erreur interne du serveur lors du filtrage")
    })
    public ResponseEntity<List<FactureResponseDto>> findFacturesByStatut(
            @Parameter(description = "Statut de paiement pour le filtrage", required = true,
                    schema = @Schema(implementation = StatutPaiement.class), example = "PAYE")
            @PathVariable("statutPaiement") StatutPaiement statutPaiement) {
        List<Facture> factures = factureService.findFacturesByStatut(statutPaiement);
        if (factures.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(factureMapper.toDtoList(factures));
    }


    @PreAuthorize("hasAnyRole('SECRETAIRE')")
    @GetMapping(path = "/statut/impayee", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "afficher les factures impayées",
            description = "Récupère les factures si impayées.")
    @ApiResponses(value = {
            @ApiResponse(description = "Factures filtrées récupérées avec succès",
                    content = @Content(schema = @Schema(implementation = FactureResponseDto.class))),
            @ApiResponse(description = "Aucune facture trouvée pour ce statut"),
            @ApiResponse(description = "Statut de paiement invalide ou inconnu"),
            @ApiResponse(description = "Erreur interne du serveur lors du filtrage")
    })
    public ResponseEntity<List<FactureResponseDto>> findAllFacturesIMPAYE(){
        List<Facture> factures = factureService.findAllFacturesIMPAYE();
        if (factures.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(factureMapper.toDtoList(factures));
    }


@PreAuthorize("hasAnyRole('SECRETAIRE')")
    @GetMapping(path = "/mode/{modePaiement}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Filtrer les factures par mode de paiement",
            description = "Récupère les factures selon leur mode de paiement (CARTE, ESPECES, VIREMENT, etc.).")
    @ApiResponses(value = {
            @ApiResponse(description = "Factures filtrées récupérées avec succès",
                    content = @Content(schema = @Schema(implementation = FactureResponseDto.class))),
            @ApiResponse(description = "Aucune facture trouvée pour ce mode"),
            @ApiResponse(description = "Mode de paiement invalide ou inconnu"),
            @ApiResponse(description = "Erreur interne du serveur lors du filtrage")
    })
    public ResponseEntity<List<FactureResponseDto>> findFacturesByModePaiement(
            @Parameter(description = "Mode de paiement pour le filtrage", required = true,
                    schema = @Schema(implementation = ModePaiement.class), example = "CARTE_BANCAIRE")
            @PathVariable("modePaiement") ModePaiement modePaiement) {
        List<Facture> factures = factureService.findFacturesByModePaiement(modePaiement);
        if (factures.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(factureMapper.toDtoList(factures));
    }



@PreAuthorize("hasAnyRole('SECRETAIRE')")
    @GetMapping(path = "/recherche/{idFacture}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir une facture par son ID",
            description = "Récupère tous les détails d'une facture spécifique, y compris les éléments facturés.")
    @ApiResponses(value = {
            @ApiResponse(description = "Facture trouvée et retournée",
                    content = @Content(schema = @Schema(implementation = FactureResponseDto.class))),
            @ApiResponse(description = "ID de facture invalide ou mal formé"),
            @ApiResponse(description = "Facture non trouvée avec l'ID spécifié"),
            @ApiResponse(description = "Erreur interne du serveur lors de la recherche")
    })
    public ResponseEntity<FactureResponseDto> findById(
            @Parameter(description = "ID unique de la facture à récupérer", required = true, example = "1")
            @PathVariable("idFacture") Long id) {

        Facture facture = factureService.findById(id);
        return ResponseEntity.ok(factureMapper.toDto(facture));
    }



@PreAuthorize("hasAnyRole('SECRETAIRE')")
    @DeleteMapping(path = "/{idFacture}")
    @Operation(summary = "Supprimer une facture",
            description = "Supprime définitivement une facture du système (opération irréversible).")
    @ApiResponses(value = {
            @ApiResponse(description = "Facture supprimée avec succès - pas de contenu retourné"),
            @ApiResponse(description = "ID de facture invalide ou mal formé"),
            @ApiResponse(description = "Facture non trouvée avec l'ID spécifié"),
            @ApiResponse(description = "Erreur interne du serveur lors de la suppression")
    })
    public ResponseEntity<Void> deleteFacture(
            @Parameter(description = "ID de la facture à supprimer", required = true, example = "1")
            @PathVariable("idFacture") Long id) {

        factureService.deleteFacture(id);
        return ResponseEntity.noContent().build();
    }


@PreAuthorize("hasAnyRole('SECRETAIRE')")
    @GetMapping(path = "/{idFacture}/patient", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir le patient associé à une facture",
            description = "Récupère les informations du patient lié à une facture spécifique.")
    @ApiResponses(value = {
            @ApiResponse(description = "Patient trouvé et retourné",
                    content = @Content(schema = @Schema(implementation = PatientResponseDto.class))),
            @ApiResponse(description = "ID de facture invalide ou mal formé"),
            @ApiResponse(description = "Facture ou patient associé non trouvé"),
            @ApiResponse(description = "Erreur interne du serveur lors de la recherche")
    })
    public ResponseEntity<PatientResponseDto> findPatientByFactureId(
            @Parameter(description = "ID de la facture pour trouver le patient associé", required = true, example = "1")
            @PathVariable("idFacture") Long id) {

        Patient patient = factureService.findPatientByFactureId(id);
        return ResponseEntity.ok(patientMapper.toDto(patient));
    }
  
    
//@PreAuthorize("hasAnyRole('SECRETAIRE')")
//    @PatchMapping(path = "/{idFacture}/statut/{nouveauStatut}", produces = MediaType.APPLICATION_JSON_VALUE)
//    @Operation(summary = "Mettre à jour le statut de paiement",
//            description = "Modifie uniquement le statut de paiement d'une facture existante (PAYE, IMPAYE, etc.).")
//    @ApiResponses(value = {
//            @ApiResponse(description = "Statut mis à jour avec succès",
//                    content = @Content(schema = @Schema(implementation = FactureResponseDto.class))),
//            @ApiResponse(description = "ID de facture ou statut invalide"),
//            @ApiResponse(description = "Facture non trouvée avec l'ID spécifié"),
//            @ApiResponse(description = "Erreur interne du serveur lors de la mise à jour")
//    })
//    public ResponseEntity<FactureResponseDto> updateStatutPaiement(
//            @Parameter(description = "ID de la facture à mettre à jour", required = true, example = "1")
//            @PathVariable("idFacture") Long id,
//            @Parameter(description = "Nouveau statut de paiement", required = true,
//                    schema = @Schema(implementation = StatutPaiement.class), example = "PAYE")
//            @PathVariable("nouveauStatut") StatutPaiement nouveauStatut) {
//
//        Facture updatedFacture = factureService.updateStatutPaiement(id, nouveauStatut);
//        return ResponseEntity.ok(factureMapper.toDto(updatedFacture));
//    }


@PreAuthorize("hasAnyRole('SECRETAIRE')")
    @PatchMapping(path = "/payer/{factureId}/{modePaiement}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Marquer une facture comme payée",
            description = "Met à jour le statut d'une facture IMPAYEE à PAYEE.")
    @ApiResponses(value = {
            @ApiResponse(description = "Facture marquée comme payée avec succès",
                    content = @Content(schema = @Schema(implementation = FactureResponseDto.class))),
            @ApiResponse(description = "Facture non trouvée avec l'ID spécifié"),
            @ApiResponse(description = "La facture est déjà PAYEE"),
            @ApiResponse(description = "Erreur interne du serveur lors de la mise à jour")
    })
    public ResponseEntity<FactureResponseDto> payerFacture(
            @Parameter(description = "ID de la facture à marquer comme payée", required = true, example = "1")
            @PathVariable("factureId") Long factureId, @PathVariable("modePaiement") ModePaiement modePaiement) {
        Facture updatedFacture = factureService.payerFacture(factureId, modePaiement);
        return ResponseEntity.ok(factureMapper.toDto(updatedFacture));
    }



}