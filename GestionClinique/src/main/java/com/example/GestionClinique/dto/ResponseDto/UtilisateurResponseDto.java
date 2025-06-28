package com.example.GestionClinique.dto.ResponseDto;


import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import lombok.*;


@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurResponseDto extends InfoPersonnelResponseDto {
    private String Email;
    private ServiceMedical serviceMedicalName;
    private Boolean actif;
    private RoleResponseDto roles1;
    private RoleResponseDto roles2;
}
