package com.example.GestionClinique.dto.RequestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class UpdatePasswordRequestDto {

    @NotBlank(message = "Le nouveau mot de passe est obligatoire.")
    @Size(min = 8, message = "Le nouveau mot de passe doit contenir au moins 8 caractères.")
    private String newPassword;

    @NotBlank(message = "La confirmation du mot de passe est obligatoire.")
    private String confirmPassword;
}
