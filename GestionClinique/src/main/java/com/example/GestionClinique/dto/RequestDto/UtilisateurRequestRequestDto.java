package com.example.GestionClinique.dto.RequestDto;

import com.example.GestionClinique.dto.ResponseDto.RoleResponseDto;
import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@EqualsAndHashCode(callSuper = true)
@Data
public class UtilisateurRequestRequestDto extends InfoPersonnelRequestDto {

    @NotNull
    private String Email;

    @NotNull
    private String motDePasse;

    private ServiceMedical serviceMedicalName;

    private Boolean actif;

    @NotNull
    private RoleResponseDto roles1;

    private RoleResponseDto roles2;
}