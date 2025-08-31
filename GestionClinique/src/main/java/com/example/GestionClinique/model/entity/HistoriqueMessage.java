package com.example.GestionClinique.model.entity;


import com.example.GestionClinique.model.BaseEntity;
import com.example.GestionClinique.model.entity.enumElem.Action;
import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "historique_messages")
public class HistoriqueMessage extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acteur_id", nullable = false)
    private Utilisateur acteur;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Action action;

    private String previousContent;
    private String newContent;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}