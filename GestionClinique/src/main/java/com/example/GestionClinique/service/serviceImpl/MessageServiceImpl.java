package com.example.GestionClinique.service.serviceImpl;


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
import java.util.List;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UtilisateurRepository utilisateurRepository;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository, UtilisateurRepository utilisateurRepository) {
        this.messageRepository = messageRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public Message saveMessage(Message message, Long expediteurId, Long destinataireId) {
        // Fetch Expediteur
        Utilisateur expediteur = utilisateurRepository.findById(expediteurId)
                .orElseThrow(() -> new IllegalArgumentException("Expéditeur (Utilisateur) non trouvé avec ID: " + expediteurId));
        message.setExpediteur(expediteur);

        // Fetch Destinataire
        Utilisateur destinataire = utilisateurRepository.findById(destinataireId)
                .orElseThrow(() -> new IllegalArgumentException("Destinataire (Utilisateur) non trouvé avec ID: " + destinataireId));
        message.setDestinataire(destinataire);

        // Ensure 'lu' is false on creation by default unless specified otherwise in request
        message.setLu(false); // New messages are unread by default

        return messageRepository.save(message);
    }
    // ... rest of the service methods remain the same as before ...

    @Override
    public Message updateMessage(Long id, Message messageDetails) {
        Message existingMessage = messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Message non trouvé avec ID: " + id));

        existingMessage.setContenu(messageDetails.getContenu());
        existingMessage.setLu(messageDetails.isLu());

        return messageRepository.save(existingMessage);
    }

    @Override
    @Transactional
    public Message findMessageById(Long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Message non trouvé avec ID: " + id));
    }

    @Override
    @Transactional
    public List<Message> findAllMessages() {
        return messageRepository.findAll();
    }

    @Override
    public void deleteMessageById(Long id) {
        if (!messageRepository.existsById(id)) {
            throw new IllegalArgumentException("Message non trouvé avec ID: " + id);
        }
        messageRepository.deleteById(id);
    }

    @Override
    @Transactional
    public List<Message> findMessagesBySenderId(Long senderId) {
        return messageRepository.findByExpediteurId(senderId);
    }

    @Override
    @Transactional
    public List<Message> findMessagesByReceiverId(Long receiverId) {
        return messageRepository.findByDestinataireId(receiverId);
    }

    @Override
    public Message markMessageAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message non trouvé avec ID: " + messageId));
        message.setLu(true);
        return messageRepository.save(message);
    }
}