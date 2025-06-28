package com.example.GestionClinique.dto.ResponseDto;

import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class RendezVousResponseDto extends BaseResponseDto {
    private LocalDate jour;
    private LocalTime heure;
    private StatutRDV statut;
    private String notes;
    private ServiceMedical serviceMedicalId;
    private PatientResponseDto patientId;
    private UtilisateurResponseDto medecinId;
    private SalleResponseDto salleId;
    private ConsultationResponseDto consultationId;

}