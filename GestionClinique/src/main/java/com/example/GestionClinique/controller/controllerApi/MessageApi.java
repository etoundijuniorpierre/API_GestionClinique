package com.example.GestionClinique.controller.controllerApi;

import com.example.GestionClinique.dto.ResponseDto.MessageResponseDto;
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

@Tag(name = "Gestion des Messages", description = "API pour la gestion des messages internes de la clinique")
@RequestMapping(API_NAME + "/messages")
public interface MessageApi {


    @PreAuthorize("hasAnyRole('SECRETAIRE', 'ADMIN', 'MEDECIN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Créer un nouveau message",
            description = "Enregistre un nouveau message entre utilisateurs de la clinique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Message créé avec succès",
                    content = @Content(schema = @Schema(implementation = MessageResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Données du message invalides ou incomplètes"),
            @ApiResponse(responseCode = "404", description = "Expéditeur ou destinataire introuvable"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la création")
    })
    MessageResponseDto saveMessage(
            @Parameter(description = "Détails du message à créer", required = true,
                    content = @Content(schema = @Schema(implementation = MessageResponseDto.class)))
            @RequestBody MessageResponseDto messageResponseDto);




    @PreAuthorize("hasAnyRole('SECRETAIRE', 'ADMIN', 'MEDECIN')")
    @PutMapping(path = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour un message",
            description = "Modifie le contenu d'un message existant (seul l'expéditeur peut modifier)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message mis à jour avec succès",
                    content = @Content(schema = @Schema(implementation = MessageResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "ID ou données du message invalides"),
            @ApiResponse(responseCode = "403", description = "Non autorisé: seul l'expéditeur peut modifier"),
            @ApiResponse(responseCode = "404", description = "Message introuvable"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la mise à jour")
    })
    MessageResponseDto updateMessage(
            @Parameter(description = "ID du message à mettre à jour", required = true, example = "1")
            @PathVariable("id") Integer id,
            @Parameter(description = "Nouveau contenu du message", required = true,
                    content = @Content(schema = @Schema(implementation = MessageResponseDto.class)))
            @RequestBody MessageResponseDto messageResponseDto);




    @PreAuthorize("hasAnyRole('SECRETAIRE', 'ADMIN', 'MEDECIN')")
    @GetMapping(path = "/recherche/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir un message par son ID",
            description = "Récupère un message spécifique avec tous ses détails")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message trouvé et retourné",
                    content = @Content(schema = @Schema(implementation = MessageResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "ID de message invalide"),
            @ApiResponse(responseCode = "403", description = "Accès non autorisé (destinataire ou expéditeur uniquement)"),
            @ApiResponse(responseCode = "404", description = "Message introuvable"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la récupération")
    })
    MessageResponseDto findMessageById(
            @Parameter(description = "ID du message à récupérer", required = true, example = "1")
            @PathVariable("id") Integer id);





    @PreAuthorize("hasAnyRole('SECRETAIRE', 'ADMIN', 'MEDECIN')")
    @GetMapping( path = "/recherche/allMessage",produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Lister tous les messages",
            description = "Récupère tous les messages (réservé aux administrateurs)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des messages retournée",
                    content = @Content(schema = @Schema(implementation = MessageResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Accès non autorisé (admin uniquement)"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    List<MessageResponseDto> findAllMessages();




    @PreAuthorize("hasAnyRole('SECRETAIRE', 'ADMIN', 'MEDECIN')")
    @DeleteMapping(path = "/delete/{id}")
    @Operation(summary = "Supprimer un message",
            description = "Supprime définitivement un message (expéditeur ou admin uniquement)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Message supprimé avec succès"),
            @ApiResponse(responseCode = "400", description = "ID de message invalide"),
            @ApiResponse(responseCode = "403", description = "Non autorisé (expéditeur ou admin uniquement)"),
            @ApiResponse(responseCode = "404", description = "Message introuvable"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la suppression")
    })
    void deleteMessageById(
            @Parameter(description = "ID du message à supprimer", required = true, example = "1")
            @PathVariable("id") Integer id);




    @PreAuthorize("hasAnyRole('SECRETAIRE', 'ADMIN', 'MEDECIN')")
    @GetMapping(path = "/expediteur/{senderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Messages envoyés par un utilisateur",
            description = "Récupère tous les messages envoyés par un utilisateur spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages trouvés et retournés",
                    content = @Content(schema = @Schema(implementation = MessageResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "ID expéditeur invalide"),
            @ApiResponse(responseCode = "403", description = "Accès non autorisé (propre compte uniquement)"),
            @ApiResponse(responseCode = "404", description = "Aucun message trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la recherche")
    })
    List<MessageResponseDto> findMessagesBySenderId(
            @Parameter(description = "ID de l'expéditeur", required = true, example = "1")
            @PathVariable("senderId") Integer id);





    @PreAuthorize("hasAnyRole('SECRETAIRE', 'ADMIN', 'MEDECIN')")
    @GetMapping(path = "/destinataire/{receiverId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Messages reçus par un utilisateur",
            description = "Récupère tous les messages reçus par un utilisateur spécifique")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages trouvés et retournés",
                    content = @Content(schema = @Schema(implementation = MessageResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "ID destinataire invalide"),
            @ApiResponse(responseCode = "403", description = "Accès non autorisé (propre compte uniquement)"),
            @ApiResponse(responseCode = "404", description = "Aucun message trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur lors de la recherche")
    })
    List<MessageResponseDto> findMessagesByReceiverId(
            @Parameter(description = "ID du destinataire", required = true, example = "1")
            @PathVariable("receiverId") Integer id);





    @PreAuthorize("hasAnyRole('SECRETAIRE', 'ADMIN', 'MEDECIN')")
    @PatchMapping(path = "/marquer-lu/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Marquer un message comme lu",
            description = "Marque un message spécifique comme lu par le destinataire")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message marqué comme lu",
                    content = @Content(schema = @Schema(implementation = MessageResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "ID message invalide"),
            @ApiResponse(responseCode = "403", description = "Non autorisé (destinataire uniquement)"),
            @ApiResponse(responseCode = "404", description = "Message introuvable"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    MessageResponseDto markMessageAsRead(
            @Parameter(description = "ID du message à marquer comme lu", required = true, example = "1")
            @PathVariable("id") Integer id);
}