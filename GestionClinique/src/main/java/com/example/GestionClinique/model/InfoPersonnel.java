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
    @Column(name = "nom", nullable = false)
    protected String nom;

    @Column(name = "prenom", nullable = false)
    protected String prenom;

    @Column(name = "date_naissance", nullable = false)
    protected LocalDate dateNaissance;

    @Column(name = "age")
    protected Long age;

    @Column(unique = true, name = "email")
    protected String email;

    @Column(name = "telephone", nullable = false)
    protected String telephone;

    @Column(name = "adresse", nullable = false)
    protected String adresse;

    @Column(name = "genre", nullable = false)
    protected String genre;

}
