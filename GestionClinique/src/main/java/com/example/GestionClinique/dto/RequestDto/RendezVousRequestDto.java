package com.example.GestionClinique.dto.RequestDto;// package com.example.GestionClinique.dto; // Make sure this is in the correct DTO package

import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RendezVousRequestDto {
    private LocalTime heure;
    private LocalDate jour;
    private StatutRDV statut;
    private String notes;
    private ServiceMedical serviceMedicalId;
    private Long patientId;
    private Long medecinId;
    private Long salleId;
    private Long consultationId;
}