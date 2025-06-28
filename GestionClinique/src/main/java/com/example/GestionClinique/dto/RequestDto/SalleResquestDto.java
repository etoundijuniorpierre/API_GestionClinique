package com.example.GestionClinique.dto.RequestDto;

import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import com.example.GestionClinique.model.entity.enumElem.StatutSalle;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SalleResquestDto {
    @NotBlank
    private String numero;

    @NotBlank
    private StatutSalle statutSalle;

    @NotBlank
    private ServiceMedical serviceMedical;

    private Long rendezVousId;
}