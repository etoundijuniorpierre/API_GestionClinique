package com.example.GestionClinique.service;

import com.example.GestionClinique.dto.MessageDto;
import com.example.GestionClinique.model.entity.Message;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


public interface MessageService {
    MessageDto save(MessageDto message);
    MessageDto updateMessage(Integer id, MessageDto message);
    MessageDto findMessageById(Integer id);
    List<MessageDto> findAllMessages();
    void deleteMessageById(Integer id);

    // Nouvelle méthode: Trouver les messages envoyés par un utilisateur
    List<MessageDto> findMessagesBySenderId(Integer senderId);
    // Nouvelle méthode: Trouver les messages reçus par un utilisateur
    List<MessageDto> findMessagesByReceiverId(Integer receiverId);
    // Nouvelle méthode: Marquer un message comme lu
    MessageDto markMessageAsRead(Integer messageId);
}
