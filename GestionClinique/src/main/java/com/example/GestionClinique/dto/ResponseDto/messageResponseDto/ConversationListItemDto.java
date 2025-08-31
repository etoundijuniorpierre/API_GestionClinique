package com.example.GestionClinique.dto.ResponseDto.messageResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor
public class ConversationListItemDto {
    private Long id;
    private String type;
    private String titre;
    private String lastMessagePreview;
    private LocalDateTime lastMessageAt;
    private Integer unreadCount;
}
