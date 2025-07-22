package com.example.GestionClinique.dto.RequestDto;// package com.example.GestionClinique.dto; // Make sure this is in the correct DTO package

import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RendezVousRequestDto {

    @NotNull(message = "L'ID du patient est requis.")
    private Long patientId;

    @NotNull(message = "L'heure du rendez-vous est requise.")
    private LocalTime heure;

    @NotNull(message = "La date du rendez-vous est requise.")
    @FutureOrPresent(message = "La date du rendez-vous doit être aujourd'hui ou dans le futur.")
    private LocalDate jour;

    private String notes;

    @NotNull(message = "Le service médical est requis.")
    private ServiceMedical serviceMedical;

    @NotNull(message = "L'ID du médecin est requis.")
    private Long medecinId;

}