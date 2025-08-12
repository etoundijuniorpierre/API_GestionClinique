package com.example.GestionClinique.model.entity;

import com.example.GestionClinique.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data // Keep @Data, it will generate isLu() for 'lu' field
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "message")
public class Message extends BaseEntity { // Assuming BaseEntity provides 'id', 'creationDate', 'lastModifiedDate'

    @Column(name = "contenu", nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Column(name = "lu", nullable = false)
    private boolean lu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediteur_id", nullable = false)
    private Utilisateur expediteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinataire_id")
    private Utilisateur destinataire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupe_id")
    private Groupe groupe;
}