package com.example.GestionClinique.dto.RequestDto.messageRequestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor
public class MarkReadRequestDto {
    private Long conversationId;
    private Long userId;
    private LocalDateTime readAt;
}
