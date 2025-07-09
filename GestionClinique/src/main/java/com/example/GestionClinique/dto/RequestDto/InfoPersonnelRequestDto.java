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
    private String nom;
    private String prenom;
    @Email
    private String email;
    private LocalDate dateNaissance;
    private String telephone;
    private String adresse;
    private String genre;
}
