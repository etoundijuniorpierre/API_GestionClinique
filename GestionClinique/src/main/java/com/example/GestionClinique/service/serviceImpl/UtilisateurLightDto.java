package com.example.GestionClinique.service.serviceImpl;


import com.example.GestionClinique.dto.InfoPersonnelDto;
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
public class UtilisateurLightDto {
    private Integer id;
    private InfoPersonnelDto infoPersonnel; // Assuming InfoPersonnelDto is not recursive

    // You might add other simple fields like email or full name if directly needed, e.g.:
    // private String email;
    // private String nomComplet; // You'd derive this from InfoPersonnelDto in the mapping

    public static UtilisateurLightDto fromEntity(Utilisateur utilisateur) {
        if (utilisateur == null) return null;
        return UtilisateurLightDto.builder()
                .id(utilisateur.getId())
                .infoPersonnel(InfoPersonnelDto.fromEntity(utilisateur.getInfoPersonnel()))
                .build();
    }
}
