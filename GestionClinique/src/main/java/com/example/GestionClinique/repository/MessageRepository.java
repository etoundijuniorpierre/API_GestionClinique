package com.example.GestionClinique.repository;

import com.example.GestionClinique.model.entity.Message;
import com.example.GestionClinique.model.entity.Utilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;


public interface MessageRepository extends JpaRepository<Message, Long> {
    Page<Message> findByConversationIdOrderByCreationDateDesc(Long conversationId, Pageable pageable);

    List<Message> findByConversationIdOrderByCreationDateAsc(Long conversationId);
}
