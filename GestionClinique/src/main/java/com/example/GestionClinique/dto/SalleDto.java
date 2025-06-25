package com.example.GestionClinique.dto;

import com.example.GestionClinique.model.entity.Salle;
import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import com.example.GestionClinique.model.entity.enumElem.StatutSalle;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder

public class SalleDto {
    private Integer id;
    private String numero;
    private StatutSalle statutSalle;
    private ServiceMedical serviceMedical;
    private List<RendezVousSummaryDto> rendezVous; // Used for outgoing DTOs

    public static SalleDto fromEntity(Salle salle) {
        if (salle == null) return null;

        List<RendezVousSummaryDto> rendezVousDtos = null;
        if (salle.getRendezVous() != null && !salle.getRendezVous().isEmpty()) {
            rendezVousDtos = salle.getRendezVous().stream()
                    .map(RendezVousSummaryDto::fromEntity)
                    .collect(Collectors.toList());
        }

        return SalleDto.builder()
                .id(salle.getId())
                .numero(salle.getNumero())
                .statutSalle(salle.getStatutSalle())
                .serviceMedical(salle.getServiceMedical())
                .rendezVous(rendezVousDtos) // Set the list of summary DTOs for output
                .build();
    }

    public static Salle toEntity(SalleDto salleDto) {
        if (salleDto == null) return null;
        Salle salle = new Salle();
        // ID not set for new entity creation (handled by JPA)
        salle.setNumero(salleDto.getNumero());
        salle.setStatutSalle(salleDto.getStatutSalle());
        salle.setServiceMedical(salleDto.getServiceMedical());


        return salle;
    }
}