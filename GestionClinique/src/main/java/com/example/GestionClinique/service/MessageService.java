package com.example.GestionClinique.service;


import com.example.GestionClinique.model.entity.Message;
import jakarta.validation.constraints.NotNull;

import java.util.List;


public interface MessageService {
    Message updateMessage(Long id, Message messageDetails); // Takes entity, returns entity
    Message findMessageById(Long id); // Returns entity
    List<Message> findAllMessages(); // Returns list of entities
    void deleteMessageById(Long id);

    List<Message> findMessagesBySenderId(Long senderId); // Takes Long, returns list of entities
    List<Message> findMessagesByReceiverId(Long receiverId); // Takes Long, returns list of entities
    Message markMessageAsRead(Long messageId); // Returns entity

    Message saveMessage(Message messageToSave, @NotNull(message = "L'ID de l'expéditeur est requis.") Long expediteurId, @NotNull(message = "L'ID du destinataire est requis.") Long destinataireId);
}
