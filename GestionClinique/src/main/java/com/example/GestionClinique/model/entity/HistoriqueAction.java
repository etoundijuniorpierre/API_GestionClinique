package com.example.GestionClinique.model.entity;


import com.example.GestionClinique.model.EntityAbstracte;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "historiqueAction")
public class HistoriqueAction extends EntityAbstracte {
    @Column(name = "date", nullable = false) // Ajouté nullable = false
    private LocalDate date;

    @Column(name= "action", nullable = false, columnDefinition = "TEXT") // Ajouté nullable = false et columnDefinition
    private String action;

    @ManyToOne(fetch = FetchType.LAZY) // Ajouté Lazy
    @JoinColumn(name = "utilisateur_id", nullable = false) // Ajouté nullable = false
    private Utilisateur utilisateur;
}