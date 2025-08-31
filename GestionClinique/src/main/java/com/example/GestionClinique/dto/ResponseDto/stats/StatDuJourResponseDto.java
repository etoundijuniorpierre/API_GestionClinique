package com.example.GestionClinique.dto.ResponseDto.stats;

import com.example.GestionClinique.dto.ResponseDto.BaseResponseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class StatDuJourResponseDto extends BaseResponseDto {
    private String jour;
    private Long nbrRendezVousCONFIRME;
    private Long nbrRendezANNULE;
    private Long nbrPatientEnrg;
    private Long nbrConsultation;
    private Double revenu;
}
