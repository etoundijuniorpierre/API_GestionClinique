package com.example.GestionClinique.repository;

import com.example.GestionClinique.model.entity.ConversationParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, Long> {
    Optional<ConversationParticipant> findByConversationIdAndUtilisateurId(Long conversationId, Long utilisateurId);
}
