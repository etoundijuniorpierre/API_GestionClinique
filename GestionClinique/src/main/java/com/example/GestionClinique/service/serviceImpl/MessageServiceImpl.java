package com.example.GestionClinique.service.serviceImpl;


import com.example.GestionClinique.dto.messagerieDto.MessageRequestDto;
import com.example.GestionClinique.dto.messagerieDto.MessageResponseDto;
import com.example.GestionClinique.model.entity.Groupe;
import com.example.GestionClinique.model.entity.Message;
import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.repository.MessageRepository;
import com.example.GestionClinique.repository.UtilisateurRepository;
import com.example.GestionClinique.service.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;

    public Message save(Message message) {
        return messageRepository.save(message);
    }

    public Optional<Message> findById(Long id) {
        return messageRepository.findById(id);
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();

    }

    public void deleteById(Long id) {
        messageRepository.deleteById(id);
    }

    public Message updateMessage(Long id, String contenu, Boolean lu, Utilisateur destinataire, Groupe groupe) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message introuvable"));

        if (contenu != null) {
            message.setContenu(contenu);
        }
        if (lu != null) {
            message.setLu(lu);
        }
        if (destinataire != null) {
            message.setDestinataire(destinataire);
            message.setGroupe(null);
        }
        if (groupe != null) {
            message.setGroupe(groupe);
            message.setDestinataire(null);
        }

        return messageRepository.save(message);
    }

}

