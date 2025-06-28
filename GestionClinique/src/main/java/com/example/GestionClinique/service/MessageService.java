package com.example.GestionClinique.service;

import com.example.GestionClinique.dto.ResponseDto.MessageResponseDto;

import java.util.List;


public interface MessageService {
    MessageResponseDto save(MessageResponseDto message);
    MessageResponseDto updateMessage(Integer id, MessageResponseDto message);
    MessageResponseDto findMessageById(Integer id);
    List<MessageResponseDto> findAllMessages();
    void deleteMessageById(Integer id);

    // Nouvelle méthode: Trouver les messages envoyés par un utilisateur
    List<MessageResponseDto> findMessagesBySenderId(Integer senderId);
    // Nouvelle méthode: Trouver les messages reçus par un utilisateur
    List<MessageResponseDto> findMessagesByReceiverId(Integer receiverId);
    // Nouvelle méthode: Marquer un message comme lu
    MessageResponseDto markMessageAsRead(Integer messageId);
}
