package com.example.GestionClinique.dto.ResponseDto;

import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RendezVousResponseDto extends BaseResponseDto {
    private Long patientId;
    private String patientNomComplet;
    private LocalDate jour;
    private LocalTime heure;
    private StatutRDV statut;
    private String notes;
    private ServiceMedical serviceMedical;
    private Long medecinId;
    private String medecinNomComplet;
    private Long salleId;
    private String nomSalle;
    private Long factureId;
}