package com.example.GestionClinique.dto.dtoConnexion;

import com.example.GestionClinique.dto.RequestDto.UtilisateurRequestRequestDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String jwt;
    private UtilisateurRequestRequestDto user;
}
