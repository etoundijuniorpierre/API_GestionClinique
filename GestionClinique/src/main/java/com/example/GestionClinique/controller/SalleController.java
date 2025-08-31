package com.example.GestionClinique.controller;


import com.example.GestionClinique.dto.RequestDto.SalleRequestDto;
import com.example.GestionClinique.dto.ResponseDto.SalleResponseDto;
import com.example.GestionClinique.mapper.SalleMapper;
import com.example.GestionClinique.model.entity.Salle;
import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
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

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.GestionClinique.model.entity.enumElem.StatutSalle;
import com.example.GestionClinique.service.SalleService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.example.GestionClinique.configuration.utils.Constants.API_NAME;


@Tag(name = "Gestion des Salles", description = "API pour la gestion des salles d'hôpital")
@RequestMapping(API_NAME + "/salles") // Plural and hyphenated for REST convention
@RestController
public class SalleController {

    private final SalleService salleService;
    private final SalleMapper salleMapper; // Inject the mapper

    @Autowired
    public SalleController(SalleService salleService, SalleMapper salleMapper) {
        this.salleService = salleService;
        this.salleMapper = salleMapper;
    }



@PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Créer une nouvelle salle",
            description = "Crée une nouvelle salle dans le système avec les détails fournis")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Salle créée avec succès",
                    content = @Content(schema = @Schema(implementation = SalleResponseDto.class))), // Use ResponseDto
            @ApiResponse(responseCode = "400", description = "Données de la salle invalides ou numéro de salle déjà existant"), // Clarified
            @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la création")
    })
    public ResponseEntity<SalleResponseDto> createSalle(
            @Parameter(description = "Détails de la salle à créer", required = true)
            @Valid @RequestBody SalleRequestDto salleRequestDto) {

            Salle salleToCreate = salleMapper.toEntity(salleRequestDto);
            Salle createdSalle = salleService.createSalle(salleToCreate);
            return new ResponseEntity<>(salleMapper.toDto(createdSalle), HttpStatus.CREATED);

    }



@PreAuthorize("hasAnyRole('SECRETAIRE', 'ADMIN')") // Doctors might need to view salle details
    @GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE) // Consolidated path
    @Operation(summary = "Obtenir une salle par ID",
            description = "Récupère les informations détaillées d'une salle spécifique par son ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Salle trouvée et retournée",
                    content = @Content(schema = @Schema(implementation = SalleResponseDto.class))), // Use ResponseDto
            @ApiResponse(responseCode = "400", description = "Format d'ID de salle invalide"),
            @ApiResponse(responseCode = "404", description = "Salle non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la récupération")
    })
    public ResponseEntity<SalleResponseDto> findSalleById(
            @Parameter(description = "ID de la salle à récupérer", required = true)
            @PathVariable("id") Long id) {

            Salle salle = salleService.findSalleById(id);
            return ResponseEntity.ok(salleMapper.toDto(salle));

    }



@PreAuthorize("hasAnyRole('SECRETAIRE', 'ADMIN')")
    @GetMapping(path = "/statut/{statutSalle}", produces = MediaType.APPLICATION_JSON_VALUE) // Consolidated path
    @Operation(summary = "Trouver les salles par statut",
            description = "Récupère toutes les salles correspondant au statut spécifié")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des salles retournée avec succès",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SalleResponseDto.class)))), // ArraySchema, Use ResponseDto
            @ApiResponse(responseCode = "204", description = "Aucune salle trouvée avec ce statut"),
            @ApiResponse(responseCode = "400", description = "Valeur de statut invalide"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la recherche")
    })
    public ResponseEntity<List<SalleResponseDto>> findSalleByStatut(
            @Parameter(description = "Statut pour filtrer les salles", required = true)
            @PathVariable("statutSalle") StatutSalle statutSalle) {
        List<Salle> salles = salleService.findSallesByStatut(statutSalle);
        if (salles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(salleMapper.toDtoList(salles));
    }



@PreAuthorize("hasAnyRole('SECRETAIRE')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE) // Simplified path for all salles
    @Operation(summary = "Lister toutes les salles",
            description = "Récupère une liste complète de toutes les salles du système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste complète des salles retournée",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SalleResponseDto.class)))), // ArraySchema, Use ResponseDto
            @ApiResponse(responseCode = "204", description = "Aucune salle trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la récupération")
    })
    public ResponseEntity<List<SalleResponseDto>> findAllSalle() {
        List<Salle> salles = salleService.findAllSalle();
        if (salles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(salleMapper.toDtoList(salles));
    }



@PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE) // Consolidated path
    @Operation(summary = "Mettre à jour une salle",
            description = "Met à jour les informations d'une salle existante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Salle mise à jour avec succès",
                    content = @Content(schema = @Schema(implementation = SalleResponseDto.class))), // Use ResponseDto
            @ApiResponse(responseCode = "400", description = "Données de salle invalides ou numéro de salle déjà existant"), // Clarified
            @ApiResponse(responseCode = "404", description = "Salle non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la mise à jour")
    })
    public ResponseEntity<SalleResponseDto> updateSalle(
            @Parameter(description = "ID de la salle à mettre à jour", required = true)
            @PathVariable("id") Long id,
            @Parameter(description = "Nouveaux détails de la salle", required = true)
            @Valid @RequestBody SalleRequestDto salleRequestDto) {

            Salle existingSalle = salleService.findSalleById(id); // Fetch existing
            salleMapper.updateEntityFromDto(salleRequestDto, existingSalle); // Map DTO to entity
            Salle updatedSalle = salleService.updateSalle(id, existingSalle); // Update in service
            return ResponseEntity.ok(salleMapper.toDto(updatedSalle));

    }



@PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping(path = "/{id}") // Simplified path
    @Operation(summary = "Supprimer une salle",
            description = "Supprime définitivement une salle du système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Salle supprimée avec succès"), // 204 No Content
            @ApiResponse(responseCode = "400", description = "Format d'ID de salle invalide"),
            @ApiResponse(responseCode = "404", description = "Salle non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la suppression")
    })
    public ResponseEntity<Void> deleteSalle(
            @Parameter(description = "ID de la salle à supprimer", required = true)
            @PathVariable("id") Long id) {

            salleService.deleteSalle(id);
            return ResponseEntity.noContent().build(); // 204 No Content

    }



//@PreAuthorize("hasAnyRole('SECRETAIRE')")
//@GetMapping(path = "/available", produces = MediaType.APPLICATION_JSON_VALUE) // Simplified path
//@Operation(summary = "Trouver les salles disponibles",
////            description = "Récupère une liste des salles disponibles pour un créneau horaire spécifique")
//@ApiResponses(value = {
////            @ApiResponse(responseCode = "200", description = "Liste des salles disponibles retournée",
////                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SalleResponseDto.class)))), // ArraySchema, Use ResponseDto
////            @ApiResponse(responseCode = "204", description = "Aucune salle disponible trouvée"),
////            @ApiResponse(responseCode = "400", description = "Paramètres de date/heure invalides"),
////            @ApiResponse(responseCode = "500", description = "Erreur serveur lors de la recherche")
////    })
////    public ResponseEntity<List<SalleResponseDto>> findAvailableSalles(
////            @Parameter(description = "Date et heure de début du créneau désiré", required = true,
////                    example = "2025-06-28T10:00:00") // Updated example date to current year
////            @RequestParam("dateHeureDebut") @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateHeureDebut,
////            @Parameter(description = "Durée du créneau désiré en minutes", required = true,
////                    example = "60")
////            @RequestParam("dureeMinutes") Long dureeMinutes) {
////        List<Salle> salles = salleService.findAvailableSalles(dateHeureDebut, dureeMinutes);
////        if (salles.isEmpty()) {
////            return ResponseEntity.noContent().build();
////        }
////        return ResponseEntity.ok(salleMapper.toDtoList(salles));
////    }

    
@PreAuthorize("hasAnyRole('SECRETAIRE')")
    @GetMapping(path = "/serviceMedical/{serviceMedical}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher des salles par service médical",
            description = "Récupère une liste de toutes les salles associées à un service médical spécifique.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Salles trouvées pour le service médical spécifié",
                    content = @Content(schema = @Schema(implementation = SalleResponseDto.class))),
            @ApiResponse(responseCode = "204", description = "Aucune salle trouvée pour ce service médical"),
            @ApiResponse(responseCode = "400", description = "Nom de service médical invalide"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<SalleResponseDto> findSallesByServiceMedical(
            @Parameter(description = "Nom du service médical (ex: MEDECINE_GENERALE, PEDIATRIE)", required = true, example = "CARDIOLOGIE")
            @PathVariable("serviceMedical") ServiceMedical serviceMedical) { 

            Salle salles = salleService.findSallesByServiceMedical(serviceMedical);
            if (salles==null) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(salleMapper.toDto(salles));
    }
}
