package com.example.GestionClinique.dto.RequestDto;

import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import com.example.GestionClinique.model.entity.enumElem.StatutSalle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SalleRequestDto {
    @NotBlank(message = "Le numéro de la salle est requis.")
    private String numeroSalle;

    @NotNull(message = "Le service médical est requis.")
    private ServiceMedical serviceMedical;

    @NotNull(message = "Le statut de la salle est requis.")
    private StatutSalle statutSalle;

}