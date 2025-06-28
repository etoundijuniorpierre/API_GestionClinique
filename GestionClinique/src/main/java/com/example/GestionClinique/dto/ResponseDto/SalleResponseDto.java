
package com.example.GestionClinique.dto.ResponseDto;

import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import com.example.GestionClinique.model.entity.enumElem.StatutSalle;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SalleResponseDto extends BaseResponseDto {
    private String numero;
    private StatutSalle statutSalle;
    private ServiceMedical serviceMedical;
    private RendezVousResponseDto rendezVous;
}