
package com.example.GestionClinique.dto;

import com.example.GestionClinique.model.entity.Salle;
import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import com.example.GestionClinique.model.entity.enumElem.StatutSalle;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SalleSummaryDto {
    private Integer id;
    private String numero;
    private StatutSalle statutSalle;
    private ServiceMedical serviceMedical;

    public static SalleSummaryDto fromEntity(Salle salle) {
        if (salle == null) {
            return null;
        }
        return SalleSummaryDto.builder()
                .id(salle.getId())
                .numero(salle.getNumero())
                .statutSalle(salle.getStatutSalle())
                .serviceMedical(salle.getServiceMedical())
                .build();
    }
}