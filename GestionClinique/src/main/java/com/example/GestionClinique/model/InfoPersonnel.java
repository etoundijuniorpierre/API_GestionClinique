package com.example.GestionClinique.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class InfoPersonnel extends BaseEntity {
    @Column(name = "nom") // Ajouté nullable = false
    protected String nom;

    @Column(name = "prenom") // Ajouté nullable = false
    protected String prenom;

    @Column(unique = true, name = "email") // Ajouté nullable = false
    protected String email;

    @Column(name = "date_naissance") // Ajouté nullable = false
    protected LocalDate dateNaissance;

    @Column(name = "telephone") // Ajouté nullable = false
    protected String telephone;

    @Column(name = "adresse") // Ajouté nullable = false
    protected String adresse;

    @Column(name = "genre") // Ajouté nullable = false
    protected String genre;

}
