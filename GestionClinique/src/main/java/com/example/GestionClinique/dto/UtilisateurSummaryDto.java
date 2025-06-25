package com.example.GestionClinique.dto;


import com.example.GestionClinique.model.entity.Utilisateur;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//other dto to historiqueaction pour gérer la boucle infinie dù à la relation entre utilisatuer et historiqueAction
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurSummaryDto {
    private Integer id;
    private InfoPersonnelDto infoPersonnel; // Assuming InfoPersonnelDto is not recursive

    // You might add other simple fields like email or full name if directly needed, e.g.:
    // private String email;
    // private String nomComplet; // You'd derive this from InfoPersonnelDto in the mapping

    public static UtilisateurSummaryDto fromEntity(Utilisateur utilisateur) {
        if (utilisateur == null) return null;
        return UtilisateurSummaryDto.builder()
                .id(utilisateur.getId())
                .infoPersonnel(InfoPersonnelDto.fromEntity(utilisateur.getInfoPersonnel()))
                .build();
    }
}
