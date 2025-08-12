package com.example.GestionClinique.dto.ResponseDto;

import lombok.Data;

@Data
public class UpdateMessageRequest {
    private Long id;
    private String newContent;
}

