package com.example.GestionClinique.dto.RequestDto.messageRequestDto;


import com.example.GestionClinique.model.entity.enumElem.TypeConversation;
import lombok.*;
import java.util.List;


@Data @NoArgsConstructor @AllArgsConstructor
public class ConversationRequestDto {
    private TypeConversation typeConversation;
    private Long groupeId;
    private List<Long> participantIds;
    private String titre;
}


