package com.example.GestionClinique.model.entity;

import com.example.GestionClinique.model.BaseEntity;
import com.example.GestionClinique.model.entity.enumElem.TypeConversation;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "conversations")
public class Conversation extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeConversation typeConversation;

    private String titre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupe_id")
    private Groupe groupe;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConversationParticipant> participants = new ArrayList<>();

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_message_sender_id")
    private Utilisateur lastMessageSender;
}