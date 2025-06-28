package com.example.GestionClinique.dto.RequestDto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDate;


@Data
public abstract class InfoPersonnelRequestDto {

    @NotEmpty
    private String nom;

    @NotEmpty
    private String prenom;

    @NotEmpty
    private String email;

    @NotEmpty
    private LocalDate dateNaissance;

    @NotEmpty
    private String telephone;

    @NotEmpty
    private String adresse;

    @NotEmpty
    private String genre;
}
