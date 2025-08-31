package com.example.GestionClinique.dto.ResponseDto;


import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import com.example.GestionClinique.model.entity.enumElem.StatusConnect;
import lombok.*;

import java.time.LocalDateTime;


@EqualsAndHashCode(callSuper = true)
@Data
public class UtilisateurResponseDto extends InfoPersonnelResponseDto {
    private String email;
    private ServiceMedical serviceMedicalName;
    private Boolean actif;
    private String photoProfil;
    private RoleResponseDto role;
    private LocalDateTime lastLoginDate;
    private LocalDateTime lastLogoutDate;
    private StatusConnect statusConnect;
}
