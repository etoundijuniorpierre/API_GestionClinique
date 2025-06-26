package com.example.GestionClinique.dto.dtoConnexion;

import com.example.GestionClinique.dto.UtilisateurDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String jwt;
    private UtilisateurDto user; // Les informations de l'utilisateur authentifié
}
