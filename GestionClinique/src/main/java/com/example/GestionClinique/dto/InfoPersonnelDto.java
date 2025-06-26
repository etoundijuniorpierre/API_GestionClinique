package com.example.GestionClinique.dto;

import com.example.GestionClinique.model.entity.InfoPersonnel;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor // Ajouté pour faciliter la construction
public class InfoPersonnelDto {
    private String nom;
    private String prenom;
    private String email;
    private LocalDate dateNaissance;
    private String telephone;
    private String adresse;
    private String genre;
}
