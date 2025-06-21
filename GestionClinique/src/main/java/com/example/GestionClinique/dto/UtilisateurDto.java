package com.example.GestionClinique.dto;

import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import lombok.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors; // Add this import

@Data
@Builder
@NoArgsConstructor // Add this for constructor
@AllArgsConstructor // Add this for constructor
public class UtilisateurDto {
    private Integer id;
    private InfoPersonnelDto infoPersonnel;
    // Consider adding @JsonIgnore if this DTO is used for GET operations
    // @JsonIgnore
    private String motDePasse;
    private Boolean actif;
    private List<RoleDto> roles;
    private ServiceMedical serviceMedical;

    // --- REMOVE THESE FIELDS ENTIRELY IF NOT NEEDED IN THIS DTO ---
    // private List<RendezVousDto> rendezVous;
    // private List<ConsultationDto> consultations;
    // private List<PrescriptionDto> prescriptions;
    // private List<MessageDto> messagesEnvoyes;
    // private List<MessageDto> messagesRecus;
    // private List<HistoriqueActionDto> historiqueActions;


    public static UtilisateurDto fromEntity(Utilisateur utilisateur) {
        if(utilisateur == null) return null;

        List<RoleDto> roleDtos = (utilisateur.getRole() != null) ?
                utilisateur.getRole().stream().map(RoleDto::fromEntity).collect(Collectors.toList()) : null;

        return UtilisateurDto.builder()
                .id(utilisateur.getId())
                .motDePasse(utilisateur.getMotDePasse())
                .actif(utilisateur.getActif())
                .serviceMedical(utilisateur.getServiceMedical())
                .infoPersonnel(InfoPersonnelDto.fromEntity(utilisateur.getInfoPersonnel()))
                .roles(roleDtos)
                // --- ENSURE THESE ARE NOT MAPPED HERE EITHER ---
                // No need to even declare the variables if the fields are removed from the DTO class
                .build();
    }

    public static Utilisateur toEntity(UtilisateurDto utilisateurDto) {
        if(utilisateurDto == null) return null;

        Utilisateur utilisateur = new Utilisateur();
        if (utilisateurDto.getId() != null) {
            utilisateur.setId(utilisateurDto.getId()); // Set ID for updates
        }
        utilisateur.setMotDePasse(utilisateurDto.getMotDePasse());
        utilisateur.setActif(utilisateurDto.getActif());
        utilisateur.setServiceMedical(utilisateurDto.getServiceMedical());
        utilisateur.setInfoPersonnel(InfoPersonnelDto.toEntity(utilisateurDto.getInfoPersonnel()));

        if (utilisateurDto.getRoles() != null) {
            utilisateur.setRole(
                    utilisateurDto.getRoles().stream()
                            .map(RoleDto::toEntity)
                            .collect(Collectors.toList())
            );
        }
        // If your toEntity still needs to handle incoming nested DTOs, you'll need
        // to handle the conversion and association carefully, potentially by fetching
        // existing entities by ID in your service layer, rather than converting full DTOs here.
        // For example, if rendezVous is passed in for a user creation/update:
        // if (utilisateurDto.getRendezVous() != null) {
        //     utilisateur.setRendezVous(
        //             utilisateurDto.getRendezVous().stream()
        //                     .map(RendezVousDto::toEntity)
        //                     .peek(rv -> rv.setMedecin(utilisateur))
        //                     .collect(Collectors.toList())
        //     );
        // }
        // Ensure that any `toEntity` for a related DTO does not recursively call `UtilisateurDto.toEntity`
        // if that related DTO also contains a reference to `Utilisateur`.

        return utilisateur;
    }


}