package com.example.GestionClinique.dto.RequestDto;


import com.example.GestionClinique.model.entity.enumElem.ServiceMedical;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;


@EqualsAndHashCode(callSuper = true)
@Data
public class UtilisateurRequestDto extends InfoPersonnelRequestDto {

    @NotNull
    @Size(min = 8, max = 20)
    private String password;

    private ServiceMedical serviceMedicalName;

    private Boolean actif;

    @NotNull
    private RoleRequestDto role;
}