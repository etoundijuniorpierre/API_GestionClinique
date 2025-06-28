package com.example.GestionClinique.controller;


import com.example.GestionClinique.dto.RequestDto.UtilisateurRequestDto;
import com.example.GestionClinique.dto.ResponseDto.UtilisateurResponseDto;
import com.example.GestionClinique.mapper.UtilisateurMapper;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.RoleType;
import com.example.GestionClinique.service.UtilisateurService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;

import static com.example.GestionClinique.utils.Constants.API_NAME;

@Tag(name = "Gestion des Utilisateurs", description = "API pour la gestion des utilisateurs du système")
@RequestMapping(API_NAME + "/utilisateurs") // Changed to plural for common REST convention
@RestController
public class UtilisateurController {

    private final UtilisateurService utilisateurService;
    private final UtilisateurMapper utilisateurMapper;

    public UtilisateurController(UtilisateurService utilisateurService, UtilisateurMapper utilisateurMapper) {
        this.utilisateurService = utilisateurService;
        this.utilisateurMapper = utilisateurMapper;
    }



    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE) // Removed "/createUtilisateur" from path, POST to base URL is common for creation
    @Operation(summary = "Créer un nouvel utilisateur",
            description = "Enregistre un nouvel utilisateur dans le système avec les détails fournis")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès",
                    content = @Content(schema = @Schema(implementation = UtilisateurResponseDto.class))), // Changed to ResponseDto
            @ApiResponse(responseCode = "400", description = "Données utilisateur invalides ou incomplètes"),
            @ApiResponse(responseCode = "404", description = "Ressource requise non trouvée (ex: rôle non existant)"), // Clarified 404 cause
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la création")
    })
    public ResponseEntity<UtilisateurResponseDto> createUtilisateur(
            @Parameter(description = "Détails de l'utilisateur à créer", required = true)
            @Valid @RequestBody UtilisateurRequestDto utilisateurRequestDto) { 
        Utilisateur utilisateurToCreate = utilisateurMapper.toEntity(utilisateurRequestDto); // Map DTO to Entity
        Utilisateur createdUtilisateur = utilisateurService.createUtilisateur(utilisateurToCreate); // Call service with Entity
        UtilisateurResponseDto responseDto = utilisateurMapper.toDto(createdUtilisateur); // Map Entity to Response DTO
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED); // Return 201 Created status
    }



    @PreAuthorize("hasAnyRole('ADMIN')") 
    @GetMapping(path = "/{idUtilisateur}", produces = MediaType.APPLICATION_JSON_VALUE) // Simplified path: /{id}
    @Operation(summary = "Obtenir un utilisateur par son ID",
            description = "Récupère les informations détaillées d'un utilisateur spécifique par son identifiant unique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé et retourné",
                    content = @Content(schema = @Schema(implementation = UtilisateurResponseDto.class))), // Changed to ResponseDto
            @ApiResponse(responseCode = "400", description = "Format d'ID utilisateur invalide"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé avec l'ID fourni"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    public ResponseEntity<UtilisateurResponseDto> findUtilisateurById(
            @Parameter(description = "ID de l'utilisateur à récupérer", required = true, example = "123")
            @PathVariable("idUtilisateur") Integer id) {
        Utilisateur utilisateur = utilisateurService.findUtilisateurById(id); // Get entity from service
        return ResponseEntity.ok(utilisateurMapper.toDto(utilisateur)); // Map to DTO and return 200 OK
    }



    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(path = "/nom/{nomUtilisateur}", produces = MediaType.APPLICATION_JSON_VALUE) // Simplified path: /nom/{nom}
    @Operation(summary = "Rechercher des utilisateurs par nom",
            description = "Récupère tous les utilisateurs correspondant au nom spécifié (recherche partielle)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des utilisateurs correspondants retournée",
                    content = @Content(schema = @Schema(implementation = UtilisateurResponseDto.class))), // Corrected array schema for list
            @ApiResponse(responseCode = "400", description = "Paramètre de nom invalide"),
            @ApiResponse(responseCode = "404", description = "Aucun utilisateur trouvé avec ce nom"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la recherche")
    })
    public ResponseEntity<List<UtilisateurResponseDto>> findUtilisateurByNom( // Renamed method
                                                                              @Parameter(description = "Nom à rechercher", required = true, example = "Dupont")
                                                                              @PathVariable("nomUtilisateur") String nom) {
        List<Utilisateur> utilisateurs = utilisateurService.findUtilisateurByNom(nom); // Call service
        // Consider returning 204 No Content if list is empty for GET requests
        if (utilisateurs.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.ok(utilisateurMapper.toDtoList(utilisateurs)); // Map to DTO list
    }



    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(path = "/email/{emailUtilisateur}", produces = MediaType.APPLICATION_JSON_VALUE) // Simplified path: /email/{email}
    @Operation(summary = "Rechercher un utilisateur par email",
            description = "Récupère un seul utilisateur par son adresse email unique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé et retourné",
                    content = @Content(schema = @Schema(implementation = UtilisateurResponseDto.class))), // Changed to ResponseDto
            @ApiResponse(responseCode = "400", description = "Format d'email invalide"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé avec cet email"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la recherche")
    })
    public ResponseEntity<UtilisateurResponseDto> findUtilisateurByEmail( // Renamed method
                                                                          @Parameter(description = "Adresse email à rechercher", required = true, example = "utilisateur@exemple.com")
                                                                          @PathVariable("emailUtilisateur") String email) {
        Utilisateur utilisateur = utilisateurService.findUtilisateurByEmail(email); // Call service
        return ResponseEntity.ok(utilisateurMapper.toDto(utilisateur)); // Map to DTO
    }



    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(path = "/role/{roleType}", produces = MediaType.APPLICATION_JSON_VALUE) // Simplified path
    @Operation(summary = "Rechercher des utilisateurs par rôle",
            description = "Récupère tous les utilisateurs ayant le rôle spécifié dans le système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des utilisateurs avec le rôle spécifié retournée",
                    content = @Content(schema = @Schema(implementation = UtilisateurResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Paramètre de rôle invalide"),
            @ApiResponse(responseCode = "404", description = "Aucun utilisateur trouvé avec ce rôle"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors du filtrage")
    })
    public ResponseEntity<List<UtilisateurResponseDto>> findUtilisateurByRoleType( // Renamed method
                                                                                   @Parameter(description = "Type de rôle pour filtrer", required = true, schema = @Schema(implementation = RoleType.class))
                                                                                   @PathVariable("roleType") RoleType roleType) { // Changed path variable name for clarity
        List<Utilisateur> utilisateurs = utilisateurService.findUtilisateurByRole_RoleType(roleType);
        if (utilisateurs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(utilisateurMapper.toDtoList(utilisateurs));
    }



    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE) // Simplified path: GET to base URL returns all
    @Operation(summary = "Lister tous les utilisateurs",
            description = "Récupère la liste complète de tous les utilisateurs enregistrés dans le système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste complète des utilisateurs retournée",
                    content = @Content(schema = @Schema(implementation = UtilisateurResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    public ResponseEntity<List<UtilisateurResponseDto>> findAllUtilisateur() {
        List<Utilisateur> utilisateurs = utilisateurService.findAllUtilisateur();
        if (utilisateurs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(utilisateurMapper.toDtoList(utilisateurs));
    }



    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping(path = "/{idUtilisateur}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE) // Simplified path: PUT to /{id}
    @Operation(summary = "Mettre à jour les informations d'un utilisateur",
            description = "Modifie les détails d'un utilisateur existant avec les informations fournies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur mis à jour avec succès",
                    content = @Content(schema = @Schema(implementation = UtilisateurResponseDto.class))), // Changed to ResponseDto
            @ApiResponse(responseCode = "400", description = "Données de mise à jour invalides ou ID incorrect"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé avec l'ID fourni"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la mise à jour")
    })
    public ResponseEntity<UtilisateurResponseDto> updateUtilisateur(
            @Parameter(description = "ID de l'utilisateur à mettre à jour", required = true, example = "123")
            @PathVariable("idUtilisateur") Integer id,
            @Parameter(description = "Nouveaux détails de l'utilisateur", required = true)
            @Valid @RequestBody UtilisateurRequestDto utilisateurRequestDto) { // Added @Valid
        Utilisateur existingUtilisateur = utilisateurService.findUtilisateurById(id); // Fetch existing entity
        utilisateurMapper.updateEntityFromDto(utilisateurRequestDto, existingUtilisateur); // Update entity from DTO
        Utilisateur updatedUtilisateur = utilisateurService.updateUtilisateur(id, existingUtilisateur); // Pass updated entity to service
        return ResponseEntity.ok(utilisateurMapper.toDto(updatedUtilisateur)); // Map and return
    }



    @PreAuthorize("hasAnyRole('ADMIN')")
    @PatchMapping(path = "/{idUtilisateur}/status/{isActive}", produces = MediaType.APPLICATION_JSON_VALUE) // Changed to PATCH for partial update, distinct path
    @Operation(summary = "Mettre à jour le statut d'un utilisateur",
            description = "Active ou désactive un compte utilisateur dans le système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statut utilisateur mis à jour avec succès",
                    content = @Content(schema = @Schema(implementation = UtilisateurResponseDto.class))), // Changed to ResponseDto
            @ApiResponse(responseCode = "400", description = "ID utilisateur invalide ou paramètre de statut incorrect"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé avec l'ID fourni"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la mise à jour du statut")
    })
    public ResponseEntity<UtilisateurResponseDto> updateUtilisateurStatus(
            @Parameter(description = "ID de l'utilisateur à mettre à jour", required = true, example = "123")
            @PathVariable("idUtilisateur") Integer id,
            @Parameter(description = "Nouveau statut d'activation (true pour actif, false pour inactif)", required = true, example = "true")
            @PathVariable("isActive") boolean isActive) {
        Utilisateur updatedUtilisateur = utilisateurService.updateUtilisateurStatus(id, isActive);
        return ResponseEntity.ok(utilisateurMapper.toDto(updatedUtilisateur));
    }



    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping(path = "/{idUtilisateur}") // Simplified path: DELETE to /{id}
    @Operation(summary = "Supprimer un utilisateur",
            description = "Supprime définitivement un utilisateur du système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Utilisateur supprimé avec succès"),
            @ApiResponse(responseCode = "400", description = "Format d'ID utilisateur invalide"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé avec l'ID fourni"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la suppression")
    })
    public ResponseEntity<Void> deleteUtilisateur( // Use ResponseEntity<Void> for 204 No Content
                                                   @Parameter(description = "ID de l'utilisateur à supprimer", required = true, example = "1")
                                                   @PathVariable("idUtilisateur") Integer id) {
        utilisateurService.deleteUtilisateur(id);
        return ResponseEntity.noContent().build(); // Return 204 No Content
    }
}
