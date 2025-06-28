package com.example.GestionClinique.dto.RequestDto;

import lombok.Data;


@Data
public class HistoriqueActionRequestDto {

    private String action;
    private Long utilisateur;

}