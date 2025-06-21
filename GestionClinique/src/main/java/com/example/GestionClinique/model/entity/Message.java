package com.example.GestionClinique.model.entity;

import com.example.GestionClinique.model.EntityAbstracte;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "message")
public class Message extends EntityAbstracte {

    @Column(name="contenu", nullable = false, columnDefinition = "TEXT") // Ajouté nullable = false et columnDefinition
    private String contenu;

    @Column(name="dateEnvoi", nullable = false) // Ajouté nullable = false
    private LocalDateTime dateEnvoi;

    @Column(name="lu", nullable = false) // Ajouté nullable = false
    private boolean lu;

    @ManyToOne(fetch = FetchType.LAZY) // Ajouté Lazy
    @JoinColumn(name = "expediteur_id", nullable = false) // Ajouté nullable = false
    private Utilisateur expediteur;

    @ManyToOne(fetch = FetchType.LAZY) // Ajouté Lazy
    @JoinColumn(name = "destinataire_id", nullable = false) // Ajouté nullable = false
    private Utilisateur destinataire;
}
