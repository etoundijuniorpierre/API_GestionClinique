package com.example.GestionClinique.dto.RequestDto;


import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@EqualsAndHashCode(callSuper = true)
@Data
public class UtilisateurRequestDto extends InfoPersonnelRequestDto {

    @NotNull
    @Email
    private String email;

    @NotNull
    @Size(min = 8, max = 20)
    private String motDePasse;

    private ServiceMedical serviceMedicalName;

    private Boolean actif;

    @NotNull
    private Long roleId;
}