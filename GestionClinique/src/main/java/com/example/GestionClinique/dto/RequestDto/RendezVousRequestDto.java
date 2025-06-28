package com.example.GestionClinique.dto.RequestDto;// package com.example.GestionClinique.dto; // Make sure this is in the correct DTO package

import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import com.example.GestionClinique.model.entity.enumElem.StatutRDV;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "L'heure du rendez-vous est requise.")
    private LocalTime heure;

    @NotNull(message = "La date du rendez-vous est requise.")
    @FutureOrPresent(message = "La date du rendez-vous doit être aujourd'hui ou dans le futur.")
    private LocalDate jour;

    @NotNull(message = "Le statut du rendez-vous est requis.")
    private StatutRDV statut;

    private String notes; // Optional field

    @NotNull(message = "Le service médical est requis.")
    private ServiceMedical serviceMedical; // Renamed from serviceMedicalId for clarity

    @NotNull(message = "L'ID du patient est requis.")
    private Integer patientId; // Use Integer as IDs are typically Integer in your entities

    @NotNull(message = "L'ID du médecin est requis.")
    private Integer medecinId; // Use Integer

    @NotNull(message = "L'ID de la salle est requise.")
    private Integer salleId; // Use Integer

}