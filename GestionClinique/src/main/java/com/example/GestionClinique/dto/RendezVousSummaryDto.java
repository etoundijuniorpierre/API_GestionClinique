package com.example.GestionClinique.dto;

import com.example.GestionClinique.model.entity.RendezVous;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RendezVousSummaryDto {
    private Integer id;
    private LocalDate jour;
    private LocalTime heure;
    private StatutRDV statut;
    // Do NOT include patientId/medecinId/salleId/consultationId here if their respective
    // DTOs reference RendezVous back. For simplicity, just basic info and the ID.

    public static RendezVousSummaryDto fromEntity(RendezVous rendezVous) {
        if (rendezVous == null) {
            return null;
        }
        return RendezVousSummaryDto.builder()
                .id(rendezVous.getId())
                .jour(rendezVous.getJour())
                .heure(rendezVous.getHeure())
                .statut(rendezVous.getStatut())
                .build();
    }

    // You might also need a toEntity for summary DTOs if they are used in a write DTO,
    // but typically summary DTOs are for read operations.
    public static RendezVous toEntity(RendezVousSummaryDto dto) {
        if (dto == null) return null;
        RendezVous entity = new RendezVous();
        entity.setId(dto.getId());
        entity.setJour(dto.getJour());
        entity.setHeure(dto.getHeure());
        entity.setStatut(dto.getStatut());
        // Relationships (patient, medecin, salle) would be set in the service layer
        // based on IDs, not directly from a summary DTO.
        return entity;
    }
}