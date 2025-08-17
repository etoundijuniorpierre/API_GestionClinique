package com.example.GestionClinique.controller;


import com.example.GestionClinique.dto.RequestDto.UpdatePasswordRequestDto;
import com.example.GestionClinique.dto.RequestDto.UtilisateurRequestDto;
import com.example.GestionClinique.dto.ResponseDto.RendezVousResponseDto;
import com.example.GestionClinique.dto.ResponseDto.UtilisateurResponseDto;
import com.example.GestionClinique.mapper.RendezVousMapper;
import com.example.GestionClinique.mapper.UtilisateurMapper;
import com.example.GestionClinique.model.entity.RendezVous;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.RoleType;
import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import com.example.GestionClinique.model.entity.enumElem.StatusConnect;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import com.example.GestionClinique.service.UtilisateurService;
import com.example.GestionClinique.service.photoService.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static com.example.GestionClinique.configuration.utils.Constants.API_NAME;

@Tag(name = "Gestion des Utilisateurs", description = "API pour la gestion des utilisateurs du système")
@RequestMapping(API_NAME + "/utilisateurs") // Changed to plural for common REST convention
@RestController
public class UtilisateurController {

    private final UtilisateurService utilisateurService;
    private final UtilisateurMapper utilisateurMapper;
    private final PasswordEncoder passwordEncoder;
    private final RendezVousMapper rendezVousMapper;
    private final FileStorageService fileStorageService;

    public UtilisateurController(UtilisateurService utilisateurService, UtilisateurMapper utilisateurMapper, PasswordEncoder passwordEncoder, RendezVousMapper rendezVousMapper, FileStorageService fileStorageService) {
        this.utilisateurService = utilisateurService;
        this.utilisateurMapper = utilisateurMapper;
        this.passwordEncoder = passwordEncoder;
        this.rendezVousMapper = rendezVousMapper;
        this.fileStorageService = fileStorageService;
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
         @RequestBody UtilisateurRequestDto utilisateurDto) {
    Utilisateur utilisateur = utilisateurMapper.toEntity(utilisateurDto);
    Utilisateur savedUtilisateur = utilisateurService.createUtilisateur(utilisateur);
    return ResponseEntity.status(HttpStatus.CREATED).body(utilisateurMapper.toDto(savedUtilisateur));

}


    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETAIRE', 'MEDECIN')")
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
            @PathVariable("idUtilisateur") Long id) {
        Utilisateur utilisateur = utilisateurService.findUtilisateurById(id); // Get entity from service
        return ResponseEntity.ok(utilisateurMapper.toDto(utilisateur)); // Map to DTO and return 200 OK
    }



    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETAIRE', 'MEDECIN')")
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



    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETAIRE')")
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



    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETAIRE', 'MEDECIN')")
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



    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETAIRE', 'MEDECIN')")
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
            @PathVariable("idUtilisateur") Long id,
            @Parameter(description = "Nouveaux détails de l'utilisateur", required = true)
            @Valid @RequestBody UtilisateurRequestDto utilisateurRequestDto) { // Added @Valid
        Utilisateur existingUtilisateur = utilisateurService.findUtilisateurById(id); // Fetch existing entity
        utilisateurMapper.updateEntityFromDto(utilisateurRequestDto, existingUtilisateur); // Update entity from DTO
        Utilisateur updatedUtilisateur = utilisateurService.updateUtilisateur(id, existingUtilisateur); // Pass updated entity to service
        return ResponseEntity.ok(utilisateurMapper.toDto(updatedUtilisateur)); // Map and return
    }



    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    @PatchMapping(path = "/{idUtilisateur}/{statutConnect}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour du StatutConnect d'un utilisateur",
            description = "Modifier le status d'un Utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "StatutConnect utilisateur mis à jour avec succès",
                    content = @Content(schema = @Schema(implementation = UtilisateurResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "ID utilisateur invalide ou paramètre de statut incorrect"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé avec l'ID fourni"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la mise à jour du StatutConnect")
    })
    public ResponseEntity<UtilisateurResponseDto> updateUserConnectStatus(
            @Parameter(description = "ID de l'utilisateur à mettre à jour", required = true, example = "123")
            @PathVariable("idUtilisateur") Long id,
            @Parameter(description = "Nouveau statut d'activation ", required = true)
            @PathVariable("statutConnect")StatusConnect statusConnect) {
        Utilisateur updatedUtilisateur = utilisateurService.updateUserConnectStatus(id, statusConnect);
        return ResponseEntity.ok(utilisateurMapper.toDto(updatedUtilisateur));
    }



    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETAIRE', 'MEDECIN')")
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
            @PathVariable("idUtilisateur") Long id,
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
    public Object deleteUtilisateur( // Use ResponseEntity<Void> for 204 No Content
                                                   @Parameter(description = "ID de l'utilisateur à supprimer",
                                                           required = true, example = "1")
                                                   @PathVariable("idUtilisateur") Long id) {
        utilisateurService.deleteUtilisateur(id);
        return ResponseEntity.ok("l'utilisateur avec l'ID : "+id+" supprimé avec succès"); // Return 204 No Content
    }



    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    @GetMapping("/rendezvous/medecin/search")
    @Operation(summary = "Rechercher les rendez-vous d'un médecin par terme de recherche",
            description = "Recherche les rendez-vous d'un médecin en utilisant un terme qui peut correspondre à son nom, prénom, email ou ID. Accessible par ADMIN et MEDECIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des rendez-vous récupérée avec succès",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RendezVousResponseDto.class))),
            @ApiResponse(responseCode = "204", description = "Aucun rendez-vous trouvé pour le terme de recherche donné"),
            @ApiResponse(responseCode = "400", description = "Paramètre de requête manquant ou invalide"),
            @ApiResponse(responseCode = "403", description = "Accès non autorisé")
    })
    public ResponseEntity<List<RendezVousResponseDto>> getRendezVousByMedecinSearchTerm(
            @Parameter(description = "Terme de recherche (nom, prénom, email ou ID du médecin)", required = true, example = "Dr. Dupont")
            @RequestParam String medecinSearchTerm) {
        List<RendezVous> rendezVousEntities = utilisateurService.findRendezVousByMedecinSearchTerm(medecinSearchTerm);
        List<RendezVousResponseDto> rendezVousDtos = rendezVousMapper.toDtoList(rendezVousEntities);
        if (rendezVousDtos.isEmpty()) { return ResponseEntity.noContent().build(); }
        return ResponseEntity.ok(rendezVousDtos);
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    @GetMapping("/rendezvous/medecin/status")
    @Operation(summary = "Récupérer les rendez-vous d'un médecin par son nom et statut",
            description = "Permet de filtrer les rendez-vous d'un médecin en spécifiant une partie de son nom (nom ou prénom) et un statut de rendez-vous. Accessible par ADMIN et MEDECIN.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des rendez-vous récupérée avec succès",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RendezVousResponseDto.class))),
            @ApiResponse(responseCode = "204", description = "Aucun rendez-vous trouvé pour les critères donnés"),
            @ApiResponse(responseCode = "400", description = "Paramètre de requête manquant ou invalide"),
            @ApiResponse(responseCode = "403", description = "Accès non autorisé")
    })
    public ResponseEntity<List<RendezVousResponseDto>> getRendezVousForMedecinByStatus(
            @Parameter(description = "Nom ou prénom du médecin (recherche partielle)", required = true, example = "Dr. Jean")
            @RequestParam String medecinName,
            @Parameter(description = "Statut du rendez-vous (ex: CONFIRME, ANNULE, TERMINE)", required = true, example = "CONFIRME")
            @RequestParam StatutRDV statut) {
        List<RendezVous> rendezVousEntities = utilisateurService.findRendezVousForMedecinByStatus(medecinName, statut);
        List<RendezVousResponseDto> rendezVousDtos = rendezVousMapper.toDtoList(rendezVousEntities);
        if (rendezVousDtos.isEmpty()) { return ResponseEntity.noContent().build(); }
        return ResponseEntity.ok(rendezVousDtos);
    }




    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    @GetMapping("/{medecinId}/rendez-vous/confirmed/{date}")
    @Operation(summary = "Récupérer les rendez-vous confirmés d'un médecin pour aujourd'hui",
            description = "Recherche les rendez-vous confirmés d'un médecin spécifique (par son ID) pour la date d'aujourd'hui. Accessible uniquement par les MEDECINS.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des rendez-vous récupérée avec succès",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RendezVousResponseDto.class))),
            @ApiResponse(responseCode = "204", description = "Aucun rendez-vous trouvé pour le médecin et la date d'aujourd'hui"),
            @ApiResponse(responseCode = "400", description = "ID du médecin invalide"),
            @ApiResponse(responseCode = "403", description = "Accès non autorisé (seul le médecin concerné ou un ADMIN peut accéder à ces informations)")
    })
    public ResponseEntity<List<RendezVousResponseDto>> getConfirmedRendezVousForMedecinAndDate(
            @Parameter(description = "ID du médecin", required = true)
            @PathVariable Long medecinId,
            @Parameter(description = "Date au format YYYY-MM-DD", required = true, example = "2023-12-31")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<RendezVous> rendezVous = utilisateurService.findConfirmedRendezVousForMedecinAndDate(medecinId, date);

        if (rendezVous.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(rendezVousMapper.toDtoList(rendezVous));
    }



    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    @GetMapping("/search")
    @Operation(summary = "Rechercher des utilisateurs",
            description = "Recherche des utilisateurs par nom, prénom, email, téléphone, rôle ou statut de connexion. Requiert un terme de recherche d'au moins 2 caractères.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des utilisateurs trouvés"),
            @ApiResponse(responseCode = "400", description = "Terme de recherche invalide (moins de 2 caractères)"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<?> searchUsers(
            @RequestParam @Parameter(description = "Terme à rechercher (nom, prénom, email, téléphone, rôle, ou statut de connexion). Minimum 2 caractères.") String searchTerm) {

            List<Utilisateur> utilisateurs = utilisateurService.searchUsers(searchTerm);
            if (utilisateurs.isEmpty() && (searchTerm == null || searchTerm.trim().length() < 2)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Le terme de recherche doit contenir au moins 2 caractères.");
            }
            return ResponseEntity.ok(utilisateurs);
        }


    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETAIRE', 'MEDECIN')")
    @GetMapping("/connected")
    @Operation(summary = "Lister les utilisateurs actuellement connectés",
            description = "Récupère la liste de tous les utilisateurs dont le statut de connexion est 'CONNECTE'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des utilisateurs connectés",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Utilisateur.class)))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<Utilisateur>> getConnectedUsers() {
            List<Utilisateur> connectedUsers = utilisateurService.findUsersWithStatusConnected();
            return ResponseEntity.ok(connectedUsers);
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETAIRE', 'MEDECIN')")
    @GetMapping("/disconnected")
    @Operation(summary = "Lister les utilisateurs actuellement déconnectés",
            description = "Récupère la liste de tous les utilisateurs dont le statut de connexion est 'DECONNECTE'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des utilisateurs déconnectés",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Utilisateur.class)))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<Utilisateur>> getDisconnectedUsers() {
            List<Utilisateur> disconnectedUsers = utilisateurService.findUsersWithStatusDisconnected();
            return ResponseEntity.ok(disconnectedUsers);
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    @PutMapping("/{id}/password")
    @Operation(summary = "Mettre à jour le mot de passe d'un utilisateur",
            description = "Permet de changer le mot de passe d'un utilisateur spécifique. Les deux mots de passe fournis doivent correspondre.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mot de passe mis à jour avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UtilisateurResponseDto.class))), // DTO
            @ApiResponse(responseCode = "400", description = "Requête invalide (mots de passe ne correspondent pas, trop courts, etc.)",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<?> updatePassword(
            @PathVariable @Parameter(description = "ID de l'utilisateur à mettre à jour") Long id,
            @RequestBody UpdatePasswordRequestDto passwordDto) { // Use DTO
            Utilisateur updatedUtilisateur = utilisateurService.updatePassword(
                    id, passwordDto.getNewPassword(), passwordDto.getConfirmPassword());
            return ResponseEntity.ok(utilisateurMapper.toDto(updatedUtilisateur));
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETAIRE', 'MEDECIN')")
    @GetMapping("/connected/last-activity")
    @Operation(summary = "Lister les utilisateurs connectés triés par dernière activité",
            description = "Récupère la liste des utilisateurs actuellement connectés, triés par leur date de dernière connexion (les plus récents en premier).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des utilisateurs connectés triés",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UtilisateurResponseDto.class)))), // DTO
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<UtilisateurResponseDto>> getConnectedUsersByLastActivity() {
            List<Utilisateur> users = utilisateurService.findUsersWithStatusConnectedByOrderLastConnected();
            return ResponseEntity.ok(utilisateurMapper.toDtoList(users)); // MAP TO DTO LIST
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETAIRE', 'MEDECIN')")
    @GetMapping("/disconnected/last-activity")
    @Operation(summary = "Lister les utilisateurs déconnectés triés par dernière déconnexion",
            description = "Récupère la liste des utilisateurs actuellement déconnectés, triés par leur date de dernière déconnexion (les plus récents en premier).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des utilisateurs déconnectés triés",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UtilisateurResponseDto.class)))), // DTO
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<UtilisateurResponseDto>> getDisconnectedUsersByLastActivity() {
            List<Utilisateur> users = utilisateurService.findUsersWithStatusDisconnectedByOrderLastDeConnected();
            return ResponseEntity.ok(utilisateurMapper.toDtoList(users));
    }



    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    @GetMapping("/{medecinId}/confirmed/from-today")
    @Operation(summary = "Lister les rendez-vous confirmés d'un médecin à partir d'aujourd'hui",
            description = "Récupère tous les rendez-vous confirmés pour un médecin donné, à partir du jour actuel (sans tenir compte de l'heure passée du jour), triés chronologiquement par jour.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des rendez-vous trouvés",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RendezVousResponseDto.class)))), // DTO
            @ApiResponse(responseCode = "404", description = "Médecin non trouvé (si le service vérifie l'existence du médecin)",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<RendezVousResponseDto>> getConfirmedRendezVousFromTodayForMedecin(
            @PathVariable @Parameter(description = "ID du médecin") Long medecinId) {
        List<RendezVous> rendezVousList = utilisateurService.findAllRendezVousCONFIRMEInBeginByToday(medecinId);
        return ResponseEntity.ok(rendezVousMapper.toDtoList(rendezVousList));
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    @GetMapping("/{medecinId}/confirmed/all")
    @Operation(summary = "Lister tous les rendez-vous confirmés d'un médecin",
            description = "Récupère tous les rendez-vous confirmés pour un médecin donné, sans filtre de date, triés chronologiquement par jour.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des rendez-vous trouvés",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RendezVousResponseDto.class)))), // DTO
            @ApiResponse(responseCode = "404", description = "Médecin non trouvé (si le service vérifie l'existence du médecin)",
                    content = @Content(mediaType = "text/plain", schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<RendezVousResponseDto>> getAllConfirmedRendezVousForMedecin(
            @PathVariable @Parameter(description = "ID du médecin") Long medecinId) {
        List<RendezVous> rendezVousList = utilisateurService.findAllRendezVousCONFIRMEByMedecin(medecinId);
        return ResponseEntity.ok(rendezVousMapper.toDtoList(rendezVousList));
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    @PutMapping(value = "/{userId}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Mettre à jour la photo de profil",
            description = "Permet de mettre à jour la photo de profil d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photo de profil mise à jour avec succès",
                    content = @Content(schema = @Schema(implementation = UtilisateurResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Fichier photo invalide ou manquant"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur lors du traitement de l'image")
    })
    public ResponseEntity<UtilisateurResponseDto> updatePhotoProfil(
            @Parameter(description = "ID de l'utilisateur", required = true)
            @PathVariable Long userId,
            @Parameter(description = "Fichier image de profil", required = true)
            @RequestParam("photoProfil") MultipartFile photoProfil) {

        Utilisateur updatedUtilisateur = utilisateurService.updatePhotoProfil(userId, photoProfil);
        return ResponseEntity.ok(utilisateurMapper.toDto(updatedUtilisateur));
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    @GetMapping("/{userId}/photo")
    @Operation(summary = "Récupérer la photo de profil",
            description = "Récupère la photo de profil d'un utilisateur")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Photo de profil récupérée avec succès",
                    content = @Content(mediaType = "image/jpeg")),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé ou photo inexistante"),
            @ApiResponse(responseCode = "500", description = "Erreur lors de la récupération de l'image")
    })
    public ResponseEntity<Resource> getPhotoByUserId(@PathVariable Long userId) {
        try {
            Utilisateur utilisateur = utilisateurService.findUtilisateurById(userId);
            if (utilisateur == null || utilisateur.getPhotoProfil() == null) {
                return ResponseEntity.notFound().build();
            }
            Resource file = fileStorageService.load(utilisateur.getPhotoProfil());

            String contentType = Files.probeContentType(file.getFile().toPath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                    .body(file);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    @GetMapping("/by-service/{serviceMedical}")
    @Operation(summary = "Récupérer les médecins par service médical",
            description = "Retourne la liste des médecins appartenant à un service médical spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des médecins récupérée avec succès",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UtilisateurResponseDto.class)))),
            @ApiResponse(responseCode = "204", description = "Aucun médecin trouvé pour ce service"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<UtilisateurResponseDto>> getMedecinsByService(
            @Parameter(description = "Service médical à filtrer", required = true)
            @PathVariable ServiceMedical serviceMedical) {
        List<Utilisateur> medecins = utilisateurService.getMedecinsByServiceMedical(serviceMedical);
        return ResponseEntity.ok(utilisateurMapper.toDtoList(medecins));
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    @GetMapping("/available/{serviceMedical}")
    @Operation(summary = "Rechercher des médecins disponibles",
            description = "Retourne la liste des médecins disponibles pour un service, date et heure donnés")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des médecins disponibles",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UtilisateurResponseDto.class)))),
            @ApiResponse(responseCode = "204", description = "Aucun médecin disponible pour ces critères"),
            @ApiResponse(responseCode = "400", description = "Paramètres de date/heure invalides"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<UtilisateurResponseDto>> getAvailableMedecins(
            @Parameter(description = "Service médical recherché", required = true)
            @PathVariable ServiceMedical serviceMedical,
            @Parameter(description = "Date du rendez-vous (format ISO: yyyy-MM-dd)", required = true,
                    example = "2025-06-28", schema = @Schema(type = "string", format = "date"))
            @RequestParam("jour") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Heure du rendez-vous (format ISO: HH:mm:ss)", required = true,
                    example = "14:30:00", schema = @Schema(type = "string", format = "time"))
            @RequestParam("heure") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime heure) {

        List<Utilisateur> medecins = utilisateurService.getAvailableMedecinsByServiceAndTime(
                serviceMedical, date, heure);

        return ResponseEntity.ok(utilisateurMapper.toDtoList(medecins));
    }



}
