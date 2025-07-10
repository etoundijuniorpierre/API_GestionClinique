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
public class RendezVousResponseDto extends BaseResponseDto { // Assuming BaseResponseDto has 'id'
    private LocalDate jour;
    private LocalTime heure;
    private StatutRDV statut;
    private String notes;
    private ServiceMedical serviceMedical; // Renamed
    private Long patientId; // Just the ID
    private String patientNomComplet;
    private Long medecinId; // Just the ID
    private String medecinNomComplet; // Or full name for display
    private Long salleId; // Just the ID
    private String nomSalle; // Or room name for display
}