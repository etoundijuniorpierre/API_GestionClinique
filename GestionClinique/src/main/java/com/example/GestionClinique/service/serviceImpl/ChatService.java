package com.example.GestionClinique.service.serviceImpl;

import com.example.GestionClinique.dto.RequestDto.messageRequestDto.ConversationRequestDto;
import com.example.GestionClinique.dto.RequestDto.messageRequestDto.GroupeRequestDto;
import com.example.GestionClinique.dto.RequestDto.messageRequestDto.MessageRequestDto;
import com.example.GestionClinique.model.entity.*;
import com.example.GestionClinique.model.entity.enumElem.Action;
import com.example.GestionClinique.model.entity.enumElem.TypeConversation;
import com.example.GestionClinique.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final ConversationParticipantRepository conversationParticipantRepository;
    private final MessageRepository messageRepository;
    private final HistoriqueMessageRepository historiqueMessageRepository;
    private final GroupeRepository groupeRepository;
    private final UtilisateurRepository utilisateurRepository;

    @Transactional
    public Message sendMessage(MessageRequestDto messageDto, Long senderId) {
        Utilisateur expediteur = utilisateurRepository.findById(senderId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID: " + senderId));

        Conversation conversation = conversationRepository.findById(messageDto.getConversationId())
                .orElseThrow(() -> new EntityNotFoundException("Conversation non trouvée avec l'ID: " + messageDto.getConversationId()));

        if (!conversationParticipantRepository.findByConversationIdAndUtilisateurId(conversation.getId(), expediteur.getId()).isPresent()) {
            throw new IllegalArgumentException("L'utilisateur n'est pas participant de cette conversation.");
        }

        Message message = new Message();
        message.setContenu(messageDto.getContenu());
        message.setExpediteur(expediteur);
        message.setConversation(conversation);
        message.setLu(false); // Un message est initialement non lu par les autres

        Message savedMessage = messageRepository.save(message);

        // Mise à jour de la conversation
        conversation.setLastMessageAt(LocalDateTime.now());
        conversation.setLastMessageSender(expediteur);
        conversationRepository.save(conversation);

        // Mettre à jour les compteurs de messages non lus pour tous les participants, sauf l'expéditeur
        List<ConversationParticipant> participants = conversation.getParticipants();
        for (ConversationParticipant participant : participants) {
            if (!participant.getUtilisateur().getId().equals(expediteur.getId())) {
                participant.setUnreadCount(participant.getUnreadCount() + 1);
                conversationParticipantRepository.save(participant);
            }
        }

        return savedMessage;
    }

    @Transactional
    public Message updateMessage(Long messageId, String newContent, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message non trouvé avec l'ID: " + messageId));

        if (!message.getExpediteur().getId().equals(userId)) {
            throw new SecurityException("Vous n'êtes pas autorisé à modifier ce message.");
        }

        HistoriqueMessage historique = new HistoriqueMessage();
        historique.setMessage(message);
        historique.setActeur(message.getExpediteur());
        historique.setAction(Action.MODIFIER);
        historique.setPreviousContent(message.getContenu());
        historique.setNewContent(newContent);
        historique.setTimestamp(LocalDateTime.now());
        historiqueMessageRepository.save(historique);

        message.setContenu(newContent);
        return messageRepository.save(message);
    }

    @Transactional
    public void deleteMessage(Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Message non trouvé avec l'ID: " + messageId));

        if (!message.getExpediteur().getId().equals(userId)) {
            throw new SecurityException("Vous n'êtes pas autorisé à supprimer ce message.");
        }

        HistoriqueMessage historique = new HistoriqueMessage();
        historique.setMessage(message);
        historique.setActeur(message.getExpediteur());
        historique.setAction(Action.SUPPRIMER);
        historique.setPreviousContent(message.getContenu());
        historique.setTimestamp(LocalDateTime.now());
        historiqueMessageRepository.save(historique);

        messageRepository.delete(message);
    }

    public Page<Message> getMessagesByConversation(Long conversationId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return messageRepository.findByConversationIdOrderByCreationDateDesc(conversationId, pageable);
    }

    @Transactional
    public Conversation startConversation(ConversationRequestDto conversationDto) {
        if (conversationDto.getParticipantIds().size() != 2) {
            throw new IllegalArgumentException("Une conversation directe nécessite exactement deux participants.");
        }

        Long u1 = conversationDto.getParticipantIds().get(0);
        Long u2 = conversationDto.getParticipantIds().get(1);

        Optional<Conversation> existingConversation = conversationRepository.findDirectBetween(u1, u2);
        if (existingConversation.isPresent()) {
            return existingConversation.get();
        }

        Conversation newConversation = new Conversation();
        newConversation.setTypeConversation(TypeConversation.DIRECT);
        newConversation = conversationRepository.save(newConversation);

        Utilisateur participant1 = utilisateurRepository.findById(u1)
                .orElseThrow(() -> new EntityNotFoundException("Participant non trouvé avec l'ID: " + u1));
        Utilisateur participant2 = utilisateurRepository.findById(u2)
                .orElseThrow(() -> new EntityNotFoundException("Participant non trouvé avec l'ID: " + u2));

        ConversationParticipant cp1 = new ConversationParticipant();
        cp1.setConversation(newConversation);
        cp1.setUtilisateur(participant1);

        ConversationParticipant cp2 = new ConversationParticipant();
        cp2.setConversation(newConversation);
        cp2.setUtilisateur(participant2);

        conversationParticipantRepository.saveAll(List.of(cp1, cp2));
        newConversation.getParticipants().add(cp1);
        newConversation.getParticipants().add(cp2);

        return newConversation;
    }

    public List<Conversation> getUserConversations(Long userId) {
        return conversationRepository.findAllByUser(userId);
    }

    @Transactional
    public Groupe createGroup(GroupeRequestDto groupeDto) {
        Utilisateur createur = utilisateurRepository.findById(groupeDto.getIdCreateur())
                .orElseThrow(() -> new EntityNotFoundException("Créateur non trouvé avec l'ID: " + groupeDto.getIdCreateur()));

        Groupe groupe = new Groupe();
        groupe.setNom(groupeDto.getNom());
        groupe.setDescription(groupeDto.getDescription());
        groupe.setCreateur(createur);
        groupe.getMembres().add(createur);

        List<Utilisateur> membres = utilisateurRepository.findAllById(groupeDto.getIdsMembres());
        groupe.getMembres().addAll(membres);
        Groupe savedGroupe = groupeRepository.save(groupe);

        // Crée une conversation associée au groupe
        Conversation conversation = new Conversation();
        conversation.setTypeConversation(TypeConversation.GROUP);
        conversation.setTitre(groupe.getNom());
        conversation.setGroupe(savedGroupe);
        conversationRepository.save(conversation);

        // Ajoute tous les membres du groupe comme participants de la conversation
        for (Utilisateur membre : groupe.getMembres()) {
            ConversationParticipant cp = new ConversationParticipant();
            cp.setConversation(conversation);
            cp.setUtilisateur(membre);
            conversationParticipantRepository.save(cp);
        }

        return savedGroupe;
    }

    @Transactional
    public Groupe addMemberToGroup(Long groupeId, List<Long> memberIds) {
        Groupe groupe = groupeRepository.findById(groupeId)
                .orElseThrow(() -> new EntityNotFoundException("Groupe non trouvé avec l'ID: " + groupeId));

        List<Utilisateur> newMembers = utilisateurRepository.findAllById(memberIds);
        groupe.getMembres().addAll(newMembers);

        // Ajoute les nouveaux membres à la conversation du groupe
        Conversation conversation = conversationRepository.findByGroupeId(groupeId)
                .orElseThrow(() -> new EntityNotFoundException("Conversation associée au groupe non trouvée."));

        for (Utilisateur newMember : newMembers) {
            Optional<ConversationParticipant> existingParticipant = conversationParticipantRepository.findByConversationIdAndUtilisateurId(conversation.getId(), newMember.getId());
            if (existingParticipant.isEmpty()) {
                ConversationParticipant cp = new ConversationParticipant();
                cp.setConversation(conversation);
                cp.setUtilisateur(newMember);
                conversationParticipantRepository.save(cp);
            }
        }
        return groupeRepository.save(groupe);
    }

    @Transactional
    public void markConversationAsRead(Long conversationId, Long userId) {
        ConversationParticipant participant = conversationParticipantRepository.findByConversationIdAndUtilisateurId(conversationId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Participant non trouvé dans cette conversation."));

        participant.setLastReadAt(LocalDateTime.now());
        participant.setUnreadCount(0);
        conversationParticipantRepository.save(participant);
    }

    public boolean isUserInConversation(Long conversationId, Long userId) {
        return conversationParticipantRepository.findByConversationIdAndUtilisateurId(conversationId, userId).isPresent();
    }

    public Optional<Groupe> findById(Long groupeId) {
        return groupeRepository.findById(groupeId);
    }
}