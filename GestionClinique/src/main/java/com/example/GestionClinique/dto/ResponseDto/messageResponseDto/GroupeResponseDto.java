package com.example.GestionClinique.dto.ResponseDto.messageResponseDto;


import com.example.GestionClinique.dto.ResponseDto.BaseResponseDto;
import com.example.GestionClinique.dto.ResponseDto.UtilisateurResponseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class GroupeResponseDto extends BaseResponseDto {
    private String nom;
    private String description;
    private UtilisateurResponseDto createur;
    private List<UtilisateurResponseDto> membres;
}
