package com.example.GestionClinique.service.serviceImpl;

import com.example.GestionClinique.dto.MessageDto;
import com.example.GestionClinique.dto.UtilisateurSummaryDto;
import com.example.GestionClinique.model.entity.Message;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.repository.MessageRepository;
import com.example.GestionClinique.repository.UtilisateurRepository;
import com.example.GestionClinique.service.HistoriqueActionService;
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
    private final HistoriqueActionService historiqueActionService;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository,
                              UtilisateurRepository utilisateurRepository,
                              HistoriqueActionService historiqueActionService) {
        this.messageRepository = messageRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.historiqueActionService = historiqueActionService;
    }

    @Override
    @Transactional
    public MessageDto save(MessageDto messageDto) {
        if (messageDto.getContenu() == null || messageDto.getContenu().trim().isEmpty()) {
            throw new IllegalArgumentException("Le contenu du message ne peut pas être vide.");
        }
        if (messageDto.getExpediteurSummary() == null || messageDto.getExpediteurSummary().getId() == null) {
            throw new IllegalArgumentException("L'expéditeur du message doit être spécifié.");
        }
        if (messageDto.getDestinataireSummary() == null || messageDto.getDestinataireSummary().getId() == null) {
            throw new IllegalArgumentException("Le destinataire du message doit être spécifié.");
        }

        Utilisateur expediteur = utilisateurRepository.findById(messageDto.getExpediteurSummary().getId())
                .orElseThrow(() -> new EntityNotFoundException("L'expéditeur avec l'ID " + messageDto.getExpediteurSummary().getId() + " n'existe pas ou n'est pas valide."));
        Utilisateur destinataire = utilisateurRepository.findById(messageDto.getDestinataireSummary().getId())
                .orElseThrow(() -> new EntityNotFoundException("Le destinataire avec l'ID " + messageDto.getDestinataireSummary().getId() + " n'existe pas ou n'est pas valide."));

        Message message = new Message();
        message.setContenu(messageDto.getContenu());
        message.setDateEnvoi(messageDto.getDateEnvoi() != null ? messageDto.getDateEnvoi() : LocalDateTime.now());
        message.setLu(false);

        message.setExpediteur(expediteur);
        message.setDestinataire(destinataire);

        Message savedMessageEntity = messageRepository.save(message);

        MessageDto savedMessageDto = MessageDto.fromEntity(savedMessageEntity);

        historiqueActionService.enregistrerAction(
                "Message ID " + savedMessageDto.getId() + " envoyé de " +
                        (expediteur.getInfoPersonnel() != null ? expediteur.getInfoPersonnel().getNom() : "N/A") + " à " +
                        (destinataire.getInfoPersonnel() != null ? destinataire.getInfoPersonnel().getNom() : "N/A")
        );

        return savedMessageDto;
    }

    @Override
    @Transactional
    public MessageDto updateMessage(Integer messageId, MessageDto messageDto) {
        Message existingMessage = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Le message avec l'ID " + messageId + " n'existe pas."));

        // Store old content for logging
        String oldContenu = existingMessage.getContenu();
        boolean oldLuStatus = existingMessage.getLu(); // Using getLu() for Message entity

        // Update the content if provided and different from existing
        if (messageDto.getContenu() != null && !messageDto.getContenu().trim().isEmpty() &&
                !Objects.equals(oldContenu, messageDto.getContenu())) {
            existingMessage.setContenu(messageDto.getContenu());
        }

        // Update read status if provided and different from existing
        if (messageDto.getLu() != oldLuStatus) { // Using getLu() for MessageDto
            existingMessage.setLu(messageDto.getLu()); // Using getLu() for MessageDto to set on Entity
        }

        MessageDto updatedMessage = MessageDto.fromEntity(messageRepository.save(existingMessage));

        StringBuilder logMessage = new StringBuilder("Mise à jour du message ID: " + messageId + ".");
        if (!Objects.equals(oldContenu, updatedMessage.getContenu())) {
            logMessage.append(" Contenu: (modifié)");
        }
        if (oldLuStatus != updatedMessage.getLu()) { // Using getLu() for updated MessageDto
            logMessage.append(" Statut lu: ").append(oldLuStatus).append(" -> ").append(updatedMessage.getLu()).append(".");
        }
        historiqueActionService.enregistrerAction(logMessage.toString());

        return updatedMessage;
    }

    @Override
    @Transactional()
    public MessageDto findMessageById(Integer messageId) {
        Message foundMessageEntity = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Le message avec l'ID " + messageId + " n'existe pas."));

        historiqueActionService.enregistrerAction(
                "Recherche du message ID: " + messageId
        );

        return MessageDto.fromEntity(foundMessageEntity);
    }

    @Override
    @Transactional()
    public List<MessageDto> findAllMessages() {
        List<MessageDto> allMessages = messageRepository.findAll()
                .stream()
                .filter(Objects::nonNull)
                .map(MessageDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction(
                "Affichage de tous les messages (nombre de résultats: " + allMessages.size() + ")."
        );

        return allMessages;
    }

    @Override
    @Transactional
    public void deleteMessageById(Integer messageId) {
        Message messageToDelete = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Le message avec l'ID : " + messageId + " n'existe pas et ne peut pas être supprimé."));

        String senderName = (messageToDelete.getExpediteur() != null && messageToDelete.getExpediteur().getInfoPersonnel() != null) ?
                messageToDelete.getExpediteur().getInfoPersonnel().getNom() : "N/A";
        String receiverName = (messageToDelete.getDestinataire() != null && messageToDelete.getDestinataire().getInfoPersonnel() != null) ?
                messageToDelete.getDestinataire().getInfoPersonnel().getNom() : "N/A";

        messageRepository.deleteById(messageId);

        historiqueActionService.enregistrerAction(
                "Suppression du message ID: " + messageId +
                        " (Expéditeur: " + senderName + ", Destinataire: " + receiverName + ")"
        );
    }

    @Override
    @Transactional()
    public List<MessageDto> findMessagesBySenderId(Integer expediteurId) {
        Utilisateur expediteur = utilisateurRepository.findById(expediteurId)
                .orElseThrow(() -> new EntityNotFoundException("L'expéditeur avec l'ID " + expediteurId + " n'existe pas."));

        List<MessageDto> messages = messageRepository.findByExpediteur(expediteur).stream()
                .map(MessageDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction(
                "Recherche des messages envoyés par l'expéditeur ID: " + expediteurId + " (nombre de résultats: " + messages.size() + ")"
        );

        return messages;
    }

    @Override
    @Transactional()
    public List<MessageDto> findMessagesByReceiverId(Integer destinataireId) {
        Utilisateur destinataire = utilisateurRepository.findById(destinataireId)
                .orElseThrow(() -> new EntityNotFoundException("Le destinataire avec l'ID " + destinataireId + " n'existe pas."));

        List<MessageDto> messages = messageRepository.findByDestinataire(destinataire).stream()
                .map(MessageDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction(
                "Recherche des messages reçus par le destinataire ID: " + destinataireId + " (nombre de résultats: " + messages.size() + ")"
        );

        return messages;
    }

    @Override
    @Transactional
    public MessageDto markMessageAsRead(Integer messageId) {
        Message existingMessage = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Le message avec l'ID " + messageId + " n'existe pas."));

        boolean oldLuStatus = existingMessage.getLu(); // Using getLu() for Message entity
        existingMessage.setLu(true);

        MessageDto markedMessage = MessageDto.fromEntity(messageRepository.save(existingMessage));

        historiqueActionService.enregistrerAction(
                "Message ID " + messageId + " marqué comme lu. Statut: " + oldLuStatus + " -> " + markedMessage.getLu() // Using getLu() for MessageDto
        );

        return markedMessage;
    }
}