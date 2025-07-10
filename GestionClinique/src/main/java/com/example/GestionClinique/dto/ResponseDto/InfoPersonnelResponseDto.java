package com.example.GestionClinique.dto.ResponseDto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;


@Data
@EqualsAndHashCode(callSuper = true)
public class InfoPersonnelResponseDto extends BaseResponseDto {
        private String nom;
        private String prenom;
        private String email;
        private LocalDate dateNaissance;
        private String telephone;
        private String adresse;
        private String genre;
}
