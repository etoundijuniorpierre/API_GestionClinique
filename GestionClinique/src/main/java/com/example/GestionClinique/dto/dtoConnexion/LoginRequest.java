package com.example.GestionClinique.dto.dtoConnexion;

import lombok.Data; // Nécessite Lombok

@Data
public class LoginRequest {
    private String email;
    private String password;
}
