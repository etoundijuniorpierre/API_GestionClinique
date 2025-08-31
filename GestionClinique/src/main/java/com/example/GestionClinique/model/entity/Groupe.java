package com.example.GestionClinique.model.entity;

import com.example.GestionClinique.model.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "groupes")
public class Groupe extends BaseEntity {

    @Column(name = "nom")
    private String nom;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_createur", nullable = false)
    private Utilisateur createur;

    @ManyToMany
    @JoinTable(
            name = "groupe_utilisateurs",
            joinColumns = @JoinColumn(name = "groupe_id"),
            inverseJoinColumns = @JoinColumn(name = "utilisateur_id")
    )
    private List<Utilisateur> membres = new ArrayList<>();

}

