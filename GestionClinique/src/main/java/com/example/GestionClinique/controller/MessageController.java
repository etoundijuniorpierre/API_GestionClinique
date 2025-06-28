package com.example.GestionClinique.controller;


import com.example.GestionClinique.dto.RequestDto.MessageRequestDto;
import com.example.GestionClinique.mapper.MessageMapper;
import com.example.GestionClinique.model.entity.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
import com.example.GestionClinique.dto.ResponseDto.MessageResponseDto;
import com.example.GestionClinique.service.MessageService;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;


import static com.example.GestionClinique.utils.Constants.API_NAME;

@Tag(name = "Gestion des Messages", description = "API pour la gestion des messages entre utilisateurs")
@RequestMapping(API_NAME + "/messages")
@RestController // Merge MessageController and MessageApi
public class MessageController {

    private final MessageService messageService;
    private final MessageMapper messageMapper; // Inject mapper

    @Autowired
    public MessageController(MessageService messageService, MessageMapper messageMapper) {
        this.messageService = messageService;
        this.messageMapper = messageMapper;
    }



    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')") // Adjust roles as needed
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Envoyer un nouveau message",
            description = "Crée et envoie un message d'un utilisateur à un autre.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Message envoyé avec succès",
                    content = @Content(schema = @Schema(implementation = MessageResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Données du message invalides ou incomplètes (expéditeur/destinataire non trouvés)"),
            @ApiResponse(responseCode = "404", description = "Expéditeur ou destinataire non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<MessageResponseDto> saveMessage(
            @Parameter(description = "DTO du message à envoyer", required = true,
                    content = @Content(schema = @Schema(implementation = MessageRequestDto.class)))
            @Valid @RequestBody MessageRequestDto messageRequestDto) {

            Message messageToSave = messageMapper.toEntity(messageRequestDto);
            // Pass IDs explicitly to the service for fetching Utilisateur entities
            Message savedMessage = messageService.saveMessage(messageToSave, messageRequestDto.getExpediteurId(), messageRequestDto.getDestinataireId());
            return new ResponseEntity<>(messageMapper.toDto(savedMessage), HttpStatus.CREATED);
   
    }



    @PreAuthorize("hasAnyRole('ADMIN')") // Only admin can update any message for content/lu
    @PutMapping(path = "/{idMessage}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Mettre à jour un message",
            description = "Met à jour le contenu ou le statut 'lu' d'un message existant.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message mis à jour avec succès",
                    content = @Content(schema = @Schema(implementation = MessageResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Données du message invalides"),
            @ApiResponse(responseCode = "404", description = "Message non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<MessageResponseDto> updateMessage(
            @Parameter(description = "ID du message à mettre à jour", required = true, example = "1")
            @PathVariable("idMessage") Long id,
            @Parameter(description = "DTO contenant les mises à jour du message", required = true,
                    content = @Content(schema = @Schema(implementation = MessageRequestDto.class)))
            @Valid @RequestBody MessageRequestDto messageRequestDto) {
  
            Message existingMessage = messageService.findMessageById(id); // Get existing
            messageMapper.updateEntityFromDto(messageRequestDto, existingMessage); // Update fields
            Message updatedMessage = messageService.updateMessage(id, existingMessage);
            return ResponseEntity.ok(messageMapper.toDto(updatedMessage));

    }



    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')")
    @GetMapping(path = "/{idMessage}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir un message par son ID",
            description = "Récupère les détails d'un message spécifique.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message trouvé",
                    content = @Content(schema = @Schema(implementation = MessageResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Message non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<MessageResponseDto> findMessageById(
            @Parameter(description = "ID du message à récupérer", required = true, example = "1")
            @PathVariable("idMessage") Long id) {
  
            Message message = messageService.findMessageById(id);
            return ResponseEntity.ok(messageMapper.toDto(message));

    }



    @PreAuthorize("hasAnyRole('ADMIN')") // Usually only admin or specific user can see all messages
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Lister tous les messages",
            description = "Récupère une liste de tous les messages enregistrés dans le système.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des messages retournée",
                    content = @Content(schema = @Schema(implementation = MessageResponseDto.class))),
            @ApiResponse(responseCode = "204", description = "Aucun message trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<MessageResponseDto>> findAllMessages() {
        List<Message> messages = messageService.findAllMessages();
        if (messages.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(messageMapper.toDtoList(messages));
    }



    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')") // User can see messages sent by them
    @GetMapping(path = "/sender/{senderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir les messages envoyés par un utilisateur",
            description = "Récupère tous les messages dont l'utilisateur spécifié est l'expéditeur.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages trouvés",
                    content = @Content(schema = @Schema(implementation = MessageResponseDto.class))),
            @ApiResponse(responseCode = "204", description = "Aucun message trouvé pour cet expéditeur"),
            @ApiResponse(responseCode = "404", description = "Expéditeur non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<MessageResponseDto>> findMessagesBySenderId(
            @Parameter(description = "ID de l'expéditeur", required = true, example = "1")
            @PathVariable("senderId") Long senderId) {

            List<Message> messages = messageService.findMessagesBySenderId(senderId);
            if (messages.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(messageMapper.toDtoList(messages));

    }



    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')") // User can see messages received by them
    @GetMapping(path = "/receiver/{receiverId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Obtenir les messages reçus par un utilisateur",
            description = "Récupère tous les messages dont l'utilisateur spécifié est le destinataire.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages trouvés",
                    content = @Content(schema = @Schema(implementation = MessageResponseDto.class))),
            @ApiResponse(responseCode = "204", description = "Aucun message trouvé pour ce destinataire"),
            @ApiResponse(responseCode = "404", description = "Destinataire non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<MessageResponseDto>> findMessagesByReceiverId(
            @Parameter(description = "ID du destinataire", required = true, example = "2")
            @PathVariable("receiverId") Long receiverId) {

            List<Message> messages = messageService.findMessagesByReceiverId(receiverId);
            if (messages.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(messageMapper.toDtoList(messages));

    }



    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN', 'SECRETAIRE')") // Any user can mark their message as read
    @PatchMapping(path = "/mark-as-read/{messageId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Marquer un message comme lu",
            description = "Met à jour le statut 'lu' d'un message spécifique à 'true'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message marqué comme lu avec succès",
                    content = @Content(schema = @Schema(implementation = MessageResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Message non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<MessageResponseDto> markMessageAsRead(
            @Parameter(description = "ID du message à marquer comme lu", required = true, example = "1")
            @PathVariable("messageId") Long id) {

            Message updatedMessage = messageService.markMessageAsRead(id);
            return ResponseEntity.ok(messageMapper.toDto(updatedMessage));

    }



    @PreAuthorize("hasAnyRole('ADMIN')") // Only admin can delete messages
    @DeleteMapping(path = "/{idMessage}")
    @Operation(summary = "Supprimer un message",
            description = "Supprime définitivement un message du système.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Message supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Message non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<Void> deleteMessageById(
            @Parameter(description = "ID du message à supprimer", required = true, example = "1")
            @PathVariable("idMessage") Long id) {

            messageService.deleteMessageById(id);
            return ResponseEntity.noContent().build();

    }
}