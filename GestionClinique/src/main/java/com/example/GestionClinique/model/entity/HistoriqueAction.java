package com.example.GestionClinique.model.entity;


import com.example.GestionClinique.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "historiqueAction")
public class HistoriqueAction extends BaseEntity { // Assuming BaseEntity provides 'id', 'creationDate', 'lastModifiedDate'

    @Column(name = "date_action", nullable = false) // Renamed column for clarity
    private LocalDate date;

    @Column(name= "action_description", nullable = false, columnDefinition = "TEXT") // Renamed column for clarity
    private String action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;
}