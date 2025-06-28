package com.example.GestionClinique.dto.ResponseDto;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class MessageResponseDto extends BaseResponseDto {

    private String contenu;

    private boolean lu;
}