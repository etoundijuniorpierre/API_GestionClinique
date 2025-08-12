package com.example.GestionClinique.controller;


import com.example.GestionClinique.dto.RequestDto.DeleteMessageRequest;
import com.example.GestionClinique.dto.messagerieDto.MessageRequestDto;
import com.example.GestionClinique.dto.messagerieDto.MessageEvent;
import com.example.GestionClinique.dto.ResponseDto.UpdateMessageRequest;
import com.example.GestionClinique.dto.messagerieDto.MessageUpdateRequestDto;
import com.example.GestionClinique.mapper.MessageMapper;
import com.example.GestionClinique.model.entity.Groupe;
import com.example.GestionClinique.model.entity.Message;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.repository.GroupeRepository;
import com.example.GestionClinique.repository.UtilisateurRepository;
import com.example.GestionClinique.service.serviceImpl.MessageServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import com.example.GestionClinique.dto.messagerieDto.MessageResponseDto;
import com.example.GestionClinique.service.MessageService;

import org.springframework.web.bind.annotation.RestController;


import static com.example.GestionClinique.configuration.utils.Constants.API_NAME;

@Tag(name = "Gestion des Messages", description = "API pour la gestion des messages entre utilisateurs")
@RequestMapping(API_NAME + "/messagerie")
@RestController
public class MessageController {

    private final MessageServiceImpl messageService;
    private final UtilisateurRepository utilisateurRepository;
    private final GroupeRepository groupeRepository;
    private final MessageMapper messageMapper;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageController(MessageServiceImpl messageService, UtilisateurRepository utilisateurRepository, GroupeRepository groupeRepository, MessageMapper messageMapper, SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.utilisateurRepository = utilisateurRepository;
        this.groupeRepository = groupeRepository;
        this.messageMapper = messageMapper;
        this.messagingTemplate = messagingTemplate;
    }

    // Envoi nouveau message (individuel ou groupe)
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(MessageRequestDto dto) {

        Message message = messageMapper.toEntity(dto);

        Utilisateur expediteur = utilisateurRepository.findById(dto.getExpediteurId())
                .orElseThrow(() -> new RuntimeException("Expéditeur introuvable"));
        message.setExpediteur(expediteur);

        if (dto.getGroupeId() != null) {
            Groupe groupe = groupeRepository.findById(dto.getGroupeId())
                    .orElseThrow(() -> new RuntimeException("Groupe introuvable"));
            message.setGroupe(groupe);
            message.setDestinataire(null);

            Message saved = messageService.save(message);
            MessageResponseDto response = messageMapper.toDto(saved);
            messagingTemplate.convertAndSend("/topic/group." + groupe.getId(), new MessageEvent("CREATE", response));

        } else if (dto.getDestinataireId() != null) {
            Utilisateur destinataire = utilisateurRepository.findById(dto.getDestinataireId())
                    .orElseThrow(() -> new RuntimeException("Destinataire introuvable"));
            message.setDestinataire(destinataire);
            message.setGroupe(null);

            Message saved = messageService.save(message);
            MessageResponseDto response = messageMapper.toDto(saved);

            // Envoi à destinataire et expéditeur (pour mise à jour interface)
            messagingTemplate.convertAndSend("/queue/user." + destinataire.getId(), new MessageEvent("CREATE", response));
            messagingTemplate.convertAndSend("/queue/user." + expediteur.getId(), new MessageEvent("CREATE", response));

        } else {
            throw new RuntimeException("Destinataire ou groupe requis");
        }
    }


    @GetMapping("/chat.{id}")
    public ResponseEntity<MessageResponseDto> getMessageById(@PathVariable Long id) {
        return messageService.findById(id)
                .map(message -> ResponseEntity.ok(messageMapper.toDto(message)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Mise à jour message
    @MessageMapping("/chat.updateMessage")
    public void updateMessage(MessageUpdateRequestDto dto) {

        Message existing = messageService.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Message introuvable"));

        Utilisateur destinataire = null;
        Groupe groupe = null;

        if (dto.getDestinataireId() != null) {
            destinataire = utilisateurRepository.findById(dto.getDestinataireId())
                    .orElseThrow(() -> new RuntimeException("Destinataire introuvable"));
        } else if (dto.getGroupeId() != null) {
            groupe = groupeRepository.findById(dto.getGroupeId())
                    .orElseThrow(() -> new RuntimeException("Groupe introuvable"));
        }

        Message updated = messageService.updateMessage(dto.getId(), dto.getContenu(), dto.getLu(), destinataire, groupe);
        MessageResponseDto response = messageMapper.toDto(updated);

        if (groupe != null) {
            messagingTemplate.convertAndSend("/topic/group." + groupe.getId(), new MessageEvent("UPDATE", response));
        } else if (destinataire != null) {
            messagingTemplate.convertAndSend("/queue/user." + destinataire.getId(), new MessageEvent("UPDATE", response));
            messagingTemplate.convertAndSend("/queue/user." + updated.getExpediteur().getId(), new MessageEvent("UPDATE", response));
        }
    }

    // Suppression message
    @MessageMapping("/chat.deleteMessage")
    public void deleteMessage(Long messageId) {

        Message existing = messageService.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message introuvable"));

        messageService.deleteById(messageId);
        MessageResponseDto response = messageMapper.toDto(existing);

        if (existing.getGroupe() != null) {
            messagingTemplate.convertAndSend("/topic/group." + existing.getGroupe().getId(), new MessageEvent("DELETE", response));
        } else if (existing.getDestinataire() != null) {
            messagingTemplate.convertAndSend("/queue/user." + existing.getDestinataire().getId(), new MessageEvent("DELETE", response));
            messagingTemplate.convertAndSend("/queue/user." + existing.getExpediteur().getId(), new MessageEvent("DELETE", response));
        }
    }
}
