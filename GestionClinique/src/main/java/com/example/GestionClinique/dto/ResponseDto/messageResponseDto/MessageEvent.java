package com.example.GestionClinique.dto.ResponseDto.messageResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageEvent {
    private String type;
    private MessageResponseDto message;
}

