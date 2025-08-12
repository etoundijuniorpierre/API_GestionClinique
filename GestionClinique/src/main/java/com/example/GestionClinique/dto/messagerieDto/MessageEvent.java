package com.example.GestionClinique.dto.messagerieDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageEvent {
    private String type; // "CREATE", "UPDATE", "DELETE"
    private MessageResponseDto message;
}

