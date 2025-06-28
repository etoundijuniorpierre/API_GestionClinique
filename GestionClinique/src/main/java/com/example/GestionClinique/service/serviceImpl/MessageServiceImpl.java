package com.example.GestionClinique.service.serviceImpl;

import com.example.GestionClinique.dto.ResponseDto.MessageResponseDto;
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
    public MessageResponseDto save(MessageResponseDto messageResponseDto) {
        if (messageResponseDto.getContenu() == null || messageResponseDto.getContenu().trim().isEmpty()) {
            throw new IllegalArgumentException("Le contenu du message ne peut pas être vide.");
        }
        if (messageResponseDto.getExpediteurSummary() == null || messageResponseDto.getExpediteurSummary().getId() == null) {
            throw new IllegalArgumentException("L'expéditeur du message doit être spécifié.");
        }
        if (messageResponseDto.getDestinataireSummary() == null || messageResponseDto.getDestinataireSummary().getId() == null) {
            throw new IllegalArgumentException("Le destinataire du message doit être spécifié.");
        }

        Utilisateur expediteur = utilisateurRepository.findById(messageResponseDto.getExpediteurSummary().getId())
                .orElseThrow(() -> new EntityNotFoundException("L'expéditeur avec l'ID " + messageResponseDto.getExpediteurSummary().getId() + " n'existe pas ou n'est pas valide."));
        Utilisateur destinataire = utilisateurRepository.findById(messageResponseDto.getDestinataireSummary().getId())
                .orElseThrow(() -> new EntityNotFoundException("Le destinataire avec l'ID " + messageResponseDto.getDestinataireSummary().getId() + " n'existe pas ou n'est pas valide."));

        Message message = new Message();
        message.setContenu(messageResponseDto.getContenu());
        message.setDateEnvoi(messageResponseDto.getDateEnvoi() != null ? messageResponseDto.getDateEnvoi() : LocalDateTime.now());
        message.setLu(false);

        message.setExpediteur(expediteur);
        message.setDestinataire(destinataire);

        Message savedMessageEntity = messageRepository.save(message);

        MessageResponseDto savedMessageResponseDto = MessageResponseDto.fromEntity(savedMessageEntity);

        historiqueActionService.enregistrerAction(
                "Message ID " + savedMessageResponseDto.getId() + " envoyé de " +
                        (expediteur.getInfoPersonnel() != null ? expediteur.getInfoPersonnel().getNom() : "N/A") + " à " +
                        (destinataire.getInfoPersonnel() != null ? destinataire.getInfoPersonnel().getNom() : "N/A")
        );

        return savedMessageResponseDto;
    }

    @Override
    @Transactional
    public MessageResponseDto updateMessage(Integer messageId, MessageResponseDto messageResponseDto) {
        Message existingMessage = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Le message avec l'ID " + messageId + " n'existe pas."));

        // Store old content for logging
        String oldContenu = existingMessage.getContenu();
        boolean oldLuStatus = existingMessage.getLu(); // Using getLu() for Message entity

        // Update the content if provided and different from existing
        if (messageResponseDto.getContenu() != null && !messageResponseDto.getContenu().trim().isEmpty() &&
                !Objects.equals(oldContenu, messageResponseDto.getContenu())) {
            existingMessage.setContenu(messageResponseDto.getContenu());
        }

        // Update read status if provided and different from existing
        if (messageResponseDto.getLu() != oldLuStatus) { // Using getLu() for MessageDto
            existingMessage.setLu(messageResponseDto.getLu()); // Using getLu() for MessageDto to set on Entity
        }

        MessageResponseDto updatedMessage = MessageResponseDto.fromEntity(messageRepository.save(existingMessage));

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
    public MessageResponseDto findMessageById(Integer messageId) {
        Message foundMessageEntity = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Le message avec l'ID " + messageId + " n'existe pas."));

        historiqueActionService.enregistrerAction(
                "Recherche du message ID: " + messageId
        );

        return MessageResponseDto.fromEntity(foundMessageEntity);
    }

    @Override
    @Transactional()
    public List<MessageResponseDto> findAllMessages() {
        List<MessageResponseDto> allMessages = messageRepository.findAll()
                .stream()
                .filter(Objects::nonNull)
                .map(MessageResponseDto::fromEntity)
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
    public List<MessageResponseDto> findMessagesBySenderId(Integer expediteurId) {
        Utilisateur expediteur = utilisateurRepository.findById(expediteurId)
                .orElseThrow(() -> new EntityNotFoundException("L'expéditeur avec l'ID " + expediteurId + " n'existe pas."));

        List<MessageResponseDto> messages = messageRepository.findByExpediteur(expediteur).stream()
                .map(MessageResponseDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction(
                "Recherche des messages envoyés par l'expéditeur ID: " + expediteurId + " (nombre de résultats: " + messages.size() + ")"
        );

        return messages;
    }

    @Override
    @Transactional()
    public List<MessageResponseDto> findMessagesByReceiverId(Integer destinataireId) {
        Utilisateur destinataire = utilisateurRepository.findById(destinataireId)
                .orElseThrow(() -> new EntityNotFoundException("Le destinataire avec l'ID " + destinataireId + " n'existe pas."));

        List<MessageResponseDto> messages = messageRepository.findByDestinataire(destinataire).stream()
                .map(MessageResponseDto::fromEntity)
                .collect(Collectors.toList());

        historiqueActionService.enregistrerAction(
                "Recherche des messages reçus par le destinataire ID: " + destinataireId + " (nombre de résultats: " + messages.size() + ")"
        );

        return messages;
    }

    @Override
    @Transactional
    public MessageResponseDto markMessageAsRead(Integer messageId) {
        Message existingMessage = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Le message avec l'ID " + messageId + " n'existe pas."));

        boolean oldLuStatus = existingMessage.getLu(); // Using getLu() for Message entity
        existingMessage.setLu(true);

        MessageResponseDto markedMessage = MessageResponseDto.fromEntity(messageRepository.save(existingMessage));

        historiqueActionService.enregistrerAction(
                "Message ID " + messageId + " marqué comme lu. Statut: " + oldLuStatus + " -> " + markedMessage.getLu() // Using getLu() for MessageDto
        );

        return markedMessage;
    }
}