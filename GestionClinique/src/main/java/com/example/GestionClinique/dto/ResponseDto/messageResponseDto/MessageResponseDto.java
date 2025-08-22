package com.example.GestionClinique.dto.ResponseDto.messageResponseDto;

import com.example.GestionClinique.dto.ResponseDto.BaseResponseDto;
import com.example.GestionClinique.dto.ResponseDto.UtilisateurResponseDto;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
public class MessageResponseDto extends BaseResponseDto {
    private String contenu;
    private boolean lu;
    private UtilisateurResponseDto expediteur;
    private Long conversationId;
}
