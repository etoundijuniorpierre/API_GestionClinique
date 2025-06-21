package com.example.GestionClinique.dto;


import com.example.GestionClinique.model.entity.Salle;
import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import com.example.GestionClinique.model.entity.enumElem.StatutSalle;
import lombok.*;

import java.util.List;


@Data
@Builder
public class SalleDto {
    private Integer id;
    private String numero;
    private ServiceMedical serviceMedical;
    private StatutSalle statutSalle;
    private List<RendezVousDto> rendezVous; // Ajouté

    public static SalleDto fromEntity(Salle salle) {
        if(salle == null) return null;

        // Précaution pour éviter les chargements intempestifs si Lazy et non initialisé
        List<RendezVousDto> rendezVousDtos = (salle.getRendezVous() != null) ?
                salle.getRendezVous().stream().map(RendezVousDto::fromEntity).toList() : null;

        return SalleDto.builder()
                .id(salle.getId())
                .numero(salle.getNumero())
                .serviceMedical(salle.getServiceMedical())
                .statutSalle(salle.getStatutSalle())
                .rendezVous(rendezVousDtos) // Mappé
                .build();
    }

    public static Salle toEntity(SalleDto salleDto) {
        if(salleDto == null) return null;

        Salle salle = new Salle();
        // L'ID n'est généralement pas défini ici pour la création d'une nouvelle entité.
        salle.setNumero(salleDto.getNumero());
        salle.setServiceMedical(salleDto.getServiceMedical());
        salle.setStatutSalle(salleDto.getStatutSalle()); // CORRECTION de la typo

        // Gérer la liste des DTOs
        if (salleDto.getRendezVous() != null) {
            salle.setRendezVous(
                    salleDto.getRendezVous().stream()
                            .map(RendezVousDto::toEntity)
                            .peek(rv -> rv.setSalle(salle)) // Définir la relation inverse
                            .toList()
            );
        }
        return salle;
    }
}

