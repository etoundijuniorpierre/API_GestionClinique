package com.example.GestionClinique.controller.controllerApi;


import com.example.GestionClinique.dto.RequestDto.UtilisateurRequestRequestDto;
import com.example.GestionClinique.model.entity.enumElem.RoleType;
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

@Tag(name = "Gestion des Utilisateurs", description = "API pour la gestion des utilisateurs du système")
@RequestMapping(API_NAME + "/utilisateur")
public interface UtilisateurApi {


    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping(path = "/createUtilisateur", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Créer un nouvel utilisateur",
            description = "Enregistre un nouvel utilisateur dans le système avec les détails fournis")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès",
                    content = @Content(schema = @Schema(implementation = UtilisateurRequestRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "Données utilisateur invalides ou incomplètes"),
            @ApiResponse(responseCode = "404", description = "Ressource requise non trouvée"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la création")
    })
    UtilisateurRequestRequestDto createUtilisateur(
            @Parameter(description = "Détails de l'utilisateur à créer", required = true,
                    content = @Content(schema = @Schema(implementation = UtilisateurRequestRequestDto.class)))
            @RequestBody UtilisateurRequestRequestDto utilisateurRequestDto);






    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(path = "/recherche/id/{idUtilisateur}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir un utilisateur par son ID",
            description = "Récupère les informations détaillées d'un utilisateur spécifique par son identifiant unique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé et retourné",
                    content = @Content(schema = @Schema(implementation = UtilisateurRequestRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "Format d'ID utilisateur invalide"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé avec l'ID fourni"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    UtilisateurRequestRequestDto findUtilisateurById(
            @Parameter(description = "ID de l'utilisateur à récupérer", required = true,
                    example = "123")
            @PathVariable("idUtilisateur") Integer id);





    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(path = "/recherche/nom/{nomUtilisateur}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher des utilisateurs par nom",
            description = "Récupère tous les utilisateurs correspondant au nom spécifié (recherche partielle)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des utilisateurs correspondants retournée",
                    content = @Content(schema = @Schema(implementation = UtilisateurRequestRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "Paramètre de nom invalide"),
            @ApiResponse(responseCode = "404", description = "Aucun utilisateur trouvé avec ce nom"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la recherche")
    })
    List<UtilisateurRequestRequestDto> findUtilisateurByInfoPersonnel_Nom(
            @Parameter(description = "Nom à rechercher", required = true,
                    example = "Dupont")
            @PathVariable("nomUtilisateur") String nom);






    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(path = "/recherche/email/{emailUtilisateur}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher un utilisateur par email",
            description = "Récupère un seul utilisateur par son adresse email unique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé et retourné",
                    content = @Content(schema = @Schema(implementation = UtilisateurRequestRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "Format d'email invalide"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé avec cet email"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la recherche")
    })
    UtilisateurRequestRequestDto findUtilisateurByInfoPersonnel_Email(
            @Parameter(description = "Adresse email à rechercher", required = true,
                    example = "utilisateur@exemple.com")
            @PathVariable("emailUtilisateur") String email);





    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(path = "/recherche/role/{roleUtilisateur}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Rechercher des utilisateurs par rôle",
            description = "Récupère tous les utilisateurs ayant le rôle spécifié dans le système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des utilisateurs avec le rôle spécifié retournée",
                    content = @Content(schema = @Schema(implementation = UtilisateurRequestRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "Paramètre de rôle invalide"),
            @ApiResponse(responseCode = "404", description = "Aucun utilisateur trouvé avec ce rôle"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors du filtrage")
    })
    List<UtilisateurRequestRequestDto> findUtilisateurByRole_RoleType(
            @Parameter(description = "Type de rôle pour filtrer", required = true,
                    schema = @Schema(implementation = RoleType.class))
            @PathVariable("roleUtilisateur") RoleType roleType);







    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping(path = "/recherche/allUtilisateur", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Lister tous les utilisateurs",
            description = "Récupère la liste complète de tous les utilisateurs enregistrés dans le système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste complète des utilisateurs retournée",
                    content = @Content(schema = @Schema(implementation = UtilisateurRequestRequestDto.class))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    List<UtilisateurRequestRequestDto> findAllUtilisateur();






    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping(path = "/updateUtilisateur/{idUtilisateur}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour les informations d'un utilisateur",
            description = "Modifie les détails d'un utilisateur existant avec les informations fournies")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur mis à jour avec succès",
                    content = @Content(schema = @Schema(implementation = UtilisateurRequestRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "Données de mise à jour invalides ou ID incorrect"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé avec l'ID fourni"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la mise à jour")
    })
    UtilisateurRequestRequestDto updateUtilisateur(
            @Parameter(description = "ID de l'utilisateur à mettre à jour", required = true,
                    example = "123")
            @PathVariable("idUtilisateur") Integer id,
            @Parameter(description = "Nouveaux détails de l'utilisateur", required = true,
                    content = @Content(schema = @Schema(implementation = UtilisateurRequestRequestDto.class)))
            @RequestBody UtilisateurRequestRequestDto utilisateurRequestDto);







    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping(path = "/updateUtilisateurStatus/{idUtilisateur}/{isActive}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour le statut d'un utilisateur",
            description = "Active ou désactive un compte utilisateur dans le système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statut utilisateur mis à jour avec succès",
                    content = @Content(schema = @Schema(implementation = UtilisateurRequestRequestDto.class))),
            @ApiResponse(responseCode = "400", description = "ID utilisateur invalide ou paramètre de statut incorrect"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé avec l'ID fourni"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la mise à jour du statut")
    })
    UtilisateurRequestRequestDto updateUtilisateurStatus(
            @Parameter(description = "ID de l'utilisateur à mettre à jour", required = true,
                    example = "123")
            @PathVariable("idUtilisateur") Integer id,
            @Parameter(description = "Nouveau statut d'activation (true pour actif, false pour inactif)",
                    required = true, example = "true")
            @PathVariable("isActive") boolean isActive);



    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping(path = "/delete/{idUtilisateur}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Supprimer un utilisateur",
            description = "Supprime définitivement un utilisateur du système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Utilisateur supprimé avec succès"),
            @ApiResponse(responseCode = "400", description = "Format d'ID utilisateur invalide"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé avec l'ID fourni"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la suppression")
    })
    void deleteUtilisateur(
            @Parameter(description = "ID de l'utilisateur à supprimer", required = true,
                    example = "123")
            @PathVariable("idUtilisateur") Integer id);
}