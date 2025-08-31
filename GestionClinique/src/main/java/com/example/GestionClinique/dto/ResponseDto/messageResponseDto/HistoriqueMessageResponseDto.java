package com.example.GestionClinique.dto.ResponseDto.messageResponseDto;

import com.example.GestionClinique.dto.ResponseDto.BaseResponseDto;
import com.example.GestionClinique.dto.ResponseDto.UtilisateurResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class HistoriqueMessageResponseDto extends BaseResponseDto {
    private Long messageId;
    private String action;
    private UtilisateurResponseDto acteur;
    private String previousContent;
    private String newContent;
}
