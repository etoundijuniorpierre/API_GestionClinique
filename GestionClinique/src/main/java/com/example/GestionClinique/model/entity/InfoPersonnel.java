package com.example.GestionClinique.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InfoPersonnel {
    @Column(name = "nom", nullable = false) // Ajouté nullable = false
    protected String nom;

    @Column(name = "prenom", nullable = false) // Ajouté nullable = false
    protected String prenom;

    @Column(unique = true, name = "email", nullable = false) // Ajouté nullable = false
    protected String email;

    @Column(name = "date_naissance", nullable = false) // Ajouté nullable = false
    protected LocalDate dateNaissance;

    @Column(name = "telephone", nullable = false) // Ajouté nullable = false
    protected String telephone;

    @Column(name = "adresse", nullable = false) // Ajouté nullable = false
    protected String adresse;

    @Column(name = "genre", nullable = false) // Ajouté nullable = false
    protected String genre;

}
