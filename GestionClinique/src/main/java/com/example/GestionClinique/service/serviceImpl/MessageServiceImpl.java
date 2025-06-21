package com.example.GestionClinique.service.serviceImpl;

import com.example.GestionClinique.dto.MessageDto;
import com.example.GestionClinique.model.entity.Message;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.repository.MessageRepository;
import com.example.GestionClinique.repository.UtilisateurRepository;
import com.example.GestionClinique.service.HistoriqueActionService; // Import HistoriqueActionService
import com.example.GestionClinique.service.MessageService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final HistoriqueActionService historiqueActionService; // Inject HistoriqueActionService

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository,
                              UtilisateurRepository utilisateurRepository,
                              HistoriqueActionService historiqueActionService) { // Add to constructor
        this.messageRepository = messageRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.historiqueActionService = historiqueActionService; // Initialize
    }

    @Override
    @Transactional
    public MessageDto save(MessageDto messageDto) {

        // Validation les entrées
        if (messageDto.getContenu() == null || messageDto.getContenu().trim().isEmpty()) {
            throw new IllegalArgumentException("Le contenu du message ne peut pas être vide.");
        }
        if (messageDto.getExpediteur() == null || messageDto.getExpediteur().getId() == null) {
            throw new IllegalArgumentException("L'expéditeur du message doit être spécifié.");
        }
        if (messageDto.getDestinataire() == null || messageDto.getDestinataire().getId() == null) {
            throw new IllegalArgumentException("Le destinataire du message doit être spécifié.");
        }

        if (messageDto.getDateEnvoi() == null) {
            messageDto.setDateEnvoi(LocalDateTime.now());
        }

        // Valider si les utilisateurs expéditeur et destinataire existent dans la base de données
        Utilisateur expediteur = utilisateurRepository.findById(messageDto.getExpediteur().getId())
                .orElseThrow(() -> new EntityNotFoundException("L'expéditeur avec l'ID " + messageDto.getExpediteur().getId() + " n'existe pas."));
        Utilisateur destinataire = utilisateurRepository.findById(messageDto.getDestinataire().getId())
                .orElseThrow(() -> new EntityNotFoundException("Le destinataire avec l'ID " + messageDto.getDestinataire().getId() + " n'existe pas."));

        Message message = MessageDto.toEntity(messageDto);
        // S'assurer que les références d'entité réelles sont définies, et non seulement les IDs des DTOs
        message.setExpediteur(expediteur);
        message.setDestinataire(destinataire);
        message.setLu(false); // Les nouveaux messages sont non lus par défaut

        MessageDto savedMessage = MessageDto.fromEnity(messageRepository.save(message));

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Message ID " + savedMessage.getId() + " envoyé de " + expediteur.getInfoPersonnel().getNom() + " à " + destinataire.getInfoPersonnel().getNom()
        );
        // --- Fin de l'ajout de l'historique ---

        return savedMessage;
    }



    @Override
    @Transactional
    public MessageDto updateMessage(Integer messageId, MessageDto messageDto) {
        Message existingMessage = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Le message avec l'ID " + messageId + " n'existe pas."));

        // le seul élément que l'on peut mettre à jour ici étant le contenu du message
        if (messageDto.getContenu() != null && !messageDto.getContenu().trim().isEmpty()) {
            existingMessage.setContenu(messageDto.getContenu());
        }

        MessageDto updatedMessage = MessageDto.fromEnity(messageRepository.save(existingMessage));

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Message ID " + messageId + " mis à jour."
        );
        // --- Fin de l'ajout de l'historique ---

        return updatedMessage;
    }


    @Override
    @Transactional
    public MessageDto findMessageById(Integer messageId) {
        MessageDto foundMessage = messageRepository.findById(messageId)
                .map(MessageDto::fromEnity)
                .orElseThrow(() -> new RuntimeException("message pas trouvé"));

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Recherche du message ID: " + messageId
        );
        // --- Fin de l'ajout de l'historique ---

        return foundMessage;
    }



    @Override
    @Transactional
    public List<MessageDto> findAllMessages() {
        List<MessageDto> allMessages = messageRepository.findAll()
                .stream()
                .filter(Objects::nonNull)
                .map(MessageDto::fromEnity)
                .collect(Collectors.toList());

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Affichage de tous les messages."
        );
        // --- Fin de l'ajout de l'historique ---

        return allMessages;
    }



    @Override
    @Transactional
    public void deleteMessageById(Integer messageId) {
        if (!messageRepository.existsById(messageId)) {
            throw new IllegalArgumentException("Le message n'existe pas");
        }

        messageRepository.deleteById(messageId);

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Suppression du message ID: " + messageId
        );
        // --- Fin de l'ajout de l'historique ---
    }



    @Override
    @Transactional
    public List<MessageDto> findMessagesBySenderId(Integer expediteurId) {
        // 1. Validate if the sender user exists
        Utilisateur expediteur = utilisateurRepository.findById(expediteurId)
                .orElseThrow(() -> new EntityNotFoundException("L'expéditeur avec l'ID " + expediteurId + " n'existe pas."));

        List<MessageDto> messages = messageRepository.findByExpediteur(expediteur).stream()
                .map(MessageDto::fromEnity)
                .collect(Collectors.toList());

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Recherche des messages envoyés par l'expéditeur ID: " + expediteurId
        );
        // --- Fin de l'ajout de l'historique ---

        return messages;
    }



    @Override
    @Transactional
    public List<MessageDto> findMessagesByReceiverId(Integer destinataireId) {

        Utilisateur destinataire = utilisateurRepository.findById(destinataireId)
                .orElseThrow(() -> new EntityNotFoundException("Le destinataire avec l'ID " + destinataireId + " n'existe pas."));

        List<MessageDto> messages = messageRepository.findByDestinataire(destinataire).stream()
                .map(MessageDto::fromEnity)
                .collect(Collectors.toList());

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Recherche des messages reçus par le destinataire ID: " + destinataireId
        );
        // --- Fin de l'ajout de l'historique ---

        return messages;
    }



    @Override
    @Transactional
    public MessageDto markMessageAsRead(Integer messageId) {
        // Logique pour marquer un message comme lu
        Message existingMessage = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Le message n'existe pas"));

        existingMessage.setLu(true);

        MessageDto markedMessage = MessageDto.fromEnity(messageRepository.save(existingMessage));

        // --- Ajout de l'historique ---
        historiqueActionService.enregistrerAction(
                "Message ID " + messageId + " marqué comme lu."
        );
        // --- Fin de l'ajout de l'historique ---

        return markedMessage;
    }
}