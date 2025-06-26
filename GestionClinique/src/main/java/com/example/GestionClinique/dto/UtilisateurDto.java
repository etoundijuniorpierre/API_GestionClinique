package com.example.GestionClinique.dto;

import com.example.GestionClinique.model.entity.Utilisateur;
import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurDto {
    private Integer id;
    private InfoPersonnelDto infoPersonnel;
    private String motDePasse;
    private ServiceMedical serviceMedical;
    private Boolean actif;
    private List<RoleDto> roles;

    // Add lists of IDs to represent related collections without deep nesting
    private List<Integer> rendezVousIds;
    private List<Integer> consultationIds;
    private List<Integer> prescriptionIds;
    private List<Integer> messagesEnvoyesIds;
    private List<Integer> messagesRecusIds;
    private List<Integer> historiqueActionsIds;


    public static UtilisateurDto fromEntity(Utilisateur utilisateur) {
        if (utilisateur == null) return null;

        List<RoleDto> roleDtos = null;
        if (utilisateur.getRole() != null && !utilisateur.getRole().isEmpty()) {
            roleDtos = utilisateur.getRole().stream()
                    .map(RoleDto::fromEntity)
                    .collect(Collectors.toList());
        }

        // Map collections to IDs to avoid circular dependencies
        // FIX: Changed getRendezVousList() to getRendezVous()
        List<Integer> rendezVousIds = (utilisateur.getRendezVous() != null) ?
                utilisateur.getRendezVous().stream().map(rv -> rv.getId()).collect(Collectors.toList()) : null;
        List<Integer> consultationIds = (utilisateur.getConsultations() != null) ?
                utilisateur.getConsultations().stream().map(c -> c.getId()).collect(Collectors.toList()) : null;
        List<Integer> prescriptionIds = (utilisateur.getPrescriptions() != null) ?
                utilisateur.getPrescriptions().stream().map(p -> p.getId()).collect(Collectors.toList()) : null;
        List<Integer> messagesEnvoyesIds = (utilisateur.getMessagesEnvoyes() != null) ?
                utilisateur.getMessagesEnvoyes().stream().map(m -> m.getId()).collect(Collectors.toList()) : null;
        List<Integer> messagesRecusIds = (utilisateur.getMessagesRecus() != null) ?
                utilisateur.getMessagesRecus().stream().map(m -> m.getId()).collect(Collectors.toList()) : null;
        List<Integer> historiqueActionsIds = (utilisateur.getHistoriqueActions() != null) ?
                utilisateur.getHistoriqueActions().stream().map(ha -> ha.getId()).collect(Collectors.toList()) : null;


        return UtilisateurDto.builder()
                .id(utilisateur.getId())
                .infoPersonnel(InfoPersonnelDto.fromEntity(utilisateur.getInfoPersonnel()))
                .motDePasse(null) // NEVER expose password in DTO from entity
                .serviceMedical(utilisateur.getServiceMedical())
                .actif(utilisateur.getActif())
                .roles(roleDtos)
                // Set the ID lists
                .rendezVousIds(rendezVousIds)
                .consultationIds(consultationIds)
                .prescriptionIds(prescriptionIds)
                .messagesEnvoyesIds(messagesEnvoyesIds)
                .messagesRecusIds(messagesRecusIds)
                .historiqueActionsIds(historiqueActionsIds)
                .build();
    }

    public static Utilisateur toEntity(UtilisateurDto utilisateurDto) {
        if (utilisateurDto == null) return null;
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(utilisateurDto.getId());
        if (utilisateurDto.getInfoPersonnel() != null) {
            utilisateur.setInfoPersonnel(InfoPersonnelDto.toEntity(utilisateurDto.getInfoPersonnel()));
        }
        utilisateur.setMotDePasse(utilisateurDto.getMotDePasse());
        utilisateur.setServiceMedical(utilisateurDto.getServiceMedical());
        utilisateur.setActif(utilisateurDto.getActif());

        return utilisateur;
    }
}