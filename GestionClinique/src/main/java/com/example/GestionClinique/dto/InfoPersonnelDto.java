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


    public static InfoPersonnelDto fromEntity(InfoPersonnel infoPersonnel) {
        if(infoPersonnel == null) return null;

        return InfoPersonnelDto.builder()
                .nom(infoPersonnel.getNom())
                .prenom(infoPersonnel.getPrenom())
                .email(infoPersonnel.getEmail())
                .dateNaissance(infoPersonnel.getDateNaissance())
                .telephone(infoPersonnel.getTelephone())
                .adresse(infoPersonnel.getAdresse())
                .genre(infoPersonnel.getGenre())
                .build();
    }

    public static InfoPersonnel toEntity(InfoPersonnelDto infoPersonnelDto) {
        if(infoPersonnelDto == null) return null;

        InfoPersonnel infoPersonnel = new InfoPersonnel();
        infoPersonnel.setNom(infoPersonnelDto.getNom());
        infoPersonnel.setPrenom(infoPersonnelDto.getPrenom());
        infoPersonnel.setEmail(infoPersonnelDto.getEmail());
        infoPersonnel.setDateNaissance(infoPersonnelDto.getDateNaissance());
        infoPersonnel.setTelephone(infoPersonnelDto.getTelephone());
        infoPersonnel.setAdresse(infoPersonnelDto.getAdresse());
        infoPersonnel.setGenre(infoPersonnelDto.getGenre());

        return infoPersonnel;
    }
}
