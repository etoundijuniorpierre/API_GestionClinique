package com.example.GestionClinique.dto.ResponseDto.messageResponseDto;

import com.example.GestionClinique.dto.ResponseDto.BaseResponseDto;
import com.example.GestionClinique.dto.ResponseDto.UtilisateurResponseDto;
import com.example.GestionClinique.model.entity.enumElem.TypeConversation;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

@Data @EqualsAndHashCode(callSuper = true)
public class ConversationResponseDto extends BaseResponseDto {
    private TypeConversation typeConversation;
    private String titre;
    private GroupeResponseDto groupe;
    private List<UtilisateurResponseDto> participants;
    private LocalDateTime lastMessageAt;
    private Integer unreadCount;
}
