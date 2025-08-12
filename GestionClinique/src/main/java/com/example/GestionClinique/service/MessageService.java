package com.example.GestionClinique.service;


import com.example.GestionClinique.dto.messagerieDto.MessageRequestDto;
import com.example.GestionClinique.dto.messagerieDto.MessageResponseDto;
import com.example.GestionClinique.model.entity.Groupe;
import com.example.GestionClinique.model.entity.Message;
import com.example.GestionClinique.model.entity.Utilisateur;

import java.util.List;
import java.util.Optional;


public interface MessageService {
    Message save(Message message);
    Optional<Message> findById(Long id);
    List<Message> findAll();
    void deleteById(Long id);
    Message updateMessage(Long id, String contenu, Boolean lu, Utilisateur destinataire, Groupe groupe);
}
