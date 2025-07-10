package com.example.GestionClinique.dto.RequestDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Data
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class InfoPersonnelRequestDto {
    @NotEmpty
    private String nom;

    @NotEmpty
    private String prenom;

    @NotNull
    @Email
    private String email;

    @NotNull
    private LocalDate dateNaissance;

    @NotNull
    private String telephone;

    @NotNull
    private String adresse;

    @NotNull
    private String genre;
}
