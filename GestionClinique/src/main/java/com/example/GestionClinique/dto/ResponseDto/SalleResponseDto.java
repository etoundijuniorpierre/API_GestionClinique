
package com.example.GestionClinique.dto.ResponseDto;

import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import com.example.GestionClinique.model.entity.enumElem.StatutSalle;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SalleResponseDto extends BaseResponseDto { // Assuming BaseResponseDto has 'id'
    private String numero;
    private StatutSalle statutSalle;
    private ServiceMedical serviceMedical;


    private List<Long> rendezVousIds;
}