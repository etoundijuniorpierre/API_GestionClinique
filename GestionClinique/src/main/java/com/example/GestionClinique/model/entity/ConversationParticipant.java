package com.example.GestionClinique.model.entity;


import com.example.GestionClinique.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "conversation_participants",
        uniqueConstraints = @UniqueConstraint(columnNames = {"conversation_id", "utilisateur_id"}))
public class ConversationParticipant extends BaseEntity {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;


    @Column(name = "last_read_at")
    private LocalDateTime lastReadAt;


    @Column(name = "unread_count", nullable = false)
    private int unreadCount = 0;
}