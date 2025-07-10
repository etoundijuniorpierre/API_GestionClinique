package com.example.GestionClinique.dto.ResponseDto;


import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import lombok.*;


@EqualsAndHashCode(callSuper = true)
@Data
public class UtilisateurResponseDto extends InfoPersonnelResponseDto {
    private String email;
    private ServiceMedical serviceMedicalName;
    private Boolean actif;
    private RoleResponseDto role;
}
