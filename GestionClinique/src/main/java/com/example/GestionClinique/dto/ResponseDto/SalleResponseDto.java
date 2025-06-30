
package com.example.GestionClinique.dto.ResponseDto;

import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import com.example.GestionClinique.model.entity.enumElem.StatutSalle;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SalleResponseDto extends BaseResponseDto { // Assuming BaseResponseDto has 'id'
    private String numeroSalle;
    private StatutSalle statutSalle;
    private ServiceMedical serviceMedical;
    private List<Long> rendezVousIds;
}