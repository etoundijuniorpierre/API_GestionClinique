package com.example.GestionClinique.repository;


import com.example.GestionClinique.model.entity.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;


public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    @Query("select c from Conversation c join c.participants p where p.utilisateur.id = :userId order by c.lastMessageAt desc")
    List<Conversation> findAllByUser(@Param("userId") Long userId);


    @Query("select c from Conversation c join c.participants p1 join c.participants p2 " +
            "where c.typeConversation = 'DIRECT' and p1.utilisateur.id = :u1 and p2.utilisateur.id = :u2")
    Optional<Conversation> findDirectBetween(@Param("u1") Long u1, @Param("u2") Long u2);

    Optional<Conversation> findByGroupeId(Long groupeId);
}


