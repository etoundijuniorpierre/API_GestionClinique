package com.example.GestionClinique.model.entity;

import com.example.GestionClinique.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data // Keep @Data, but the explicit getter will be preferred
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "message")
public class Message extends BaseEntity {

    @Column(name="contenu", nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Column(name="lu", nullable = false)
    private boolean lu;

    public boolean getLu() {
        return this.lu;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expediteur_id", nullable = false)
    private Utilisateur expediteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinataire_id", nullable = false)
    private Utilisateur destinataire;
}