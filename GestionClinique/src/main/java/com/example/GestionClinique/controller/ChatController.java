package com.example.GestionClinique.controller;

import com.example.GestionClinique.dto.RequestDto.messageRequestDto.ConversationRequestDto;
import com.example.GestionClinique.dto.RequestDto.messageRequestDto.GroupeRequestDto;
import com.example.GestionClinique.dto.RequestDto.messageRequestDto.MessageRequestDto;
import com.example.GestionClinique.dto.ResponseDto.messageResponseDto.ConversationResponseDto;
import com.example.GestionClinique.dto.ResponseDto.messageResponseDto.GroupeResponseDto;
import com.example.GestionClinique.dto.ResponseDto.messageResponseDto.MessageResponseDto;
import com.example.GestionClinique.mapper.ConversationMapper;
import com.example.GestionClinique.mapper.GroupeMapper;
import com.example.GestionClinique.mapper.MessageMapper;
import com.example.GestionClinique.model.entity.Conversation;
import com.example.GestionClinique.model.entity.Groupe;
import com.example.GestionClinique.model.entity.Message;
import com.example.GestionClinique.service.authService.MonUserDetailsCustom;
import com.example.GestionClinique.service.serviceImpl.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.GestionClinique.configuration.utils.Constants.API_NAME;

@RestController
@RequestMapping( API_NAME+"/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final MessageMapper messageMapper;
    private final ConversationMapper conversationMapper;
    private final GroupeMapper groupeMapper;

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof MonUserDetailsCustom) {
            return ((MonUserDetailsCustom) authentication.getPrincipal()).getId();
        }
        throw new IllegalStateException("Authenticated user (Medecin) ID not found in security context or not an Utilisateur instance.");
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETAIRE', 'MEDECIN')")
    @PostMapping("/messages")
    public ResponseEntity<MessageResponseDto> sendMessage(@Valid @RequestBody MessageRequestDto messageDto, Authentication authentication) {
        Long senderId = getAuthenticatedUserId();
        Message message = chatService.sendMessage(messageDto, senderId);
        return new ResponseEntity<>(messageMapper.toDto(message), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETAIRE', 'MEDECIN')")
    @PutMapping("/messages/{id}")
    public ResponseEntity<MessageResponseDto> updateMessage(@PathVariable("id") Long id, @RequestBody String newContent, Authentication authentication) {
        Long userId = getAuthenticatedUserId();
        Message message = chatService.updateMessage(id, newContent, userId);
        return new ResponseEntity<>(messageMapper.toDto(message), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETAIRE', 'MEDECIN')")
    @DeleteMapping("/messages/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable("id") Long id, Authentication authentication) {
        Long userId = getAuthenticatedUserId();
        chatService.deleteMessage(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETAIRE', 'MEDECIN')")
    @GetMapping("/conversations/{id}/messages")
    public ResponseEntity<Page<MessageResponseDto>> getMessagesByConversation(
            @PathVariable("id") Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        Long userId = getAuthenticatedUserId();
        // Assurez-vous que l'utilisateur est bien un participant de la conversation
        if (!chatService.isUserInConversation(conversationId, userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Page<Message> messages = chatService.getMessagesByConversation(conversationId, page, size);
        Page<MessageResponseDto> dtos = messages.map(messageMapper::toDto);
        return ResponseEntity.ok(dtos);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETAIRE', 'MEDECIN')")
    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationResponseDto>> getUserConversations(Authentication authentication) {
        Long userId = getAuthenticatedUserId();
        List<Conversation> conversations = chatService.getUserConversations(userId);
        return ResponseEntity.ok(conversationMapper.toDtoList(conversations));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETAIRE', 'MEDECIN')")
    @PostMapping("/conversations")
    public ResponseEntity<ConversationResponseDto> startConversation(@Valid @RequestBody ConversationRequestDto conversationDto) {
        Conversation conversation = chatService.startConversation(conversationDto);
        return new ResponseEntity<>(conversationMapper.toDto(conversation), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SECRETAIRE', 'MEDECIN')")
    @PostMapping("/groups")
    public ResponseEntity<GroupeResponseDto> createGroup(@Valid @RequestBody GroupeRequestDto groupeDto) {
        Groupe groupe = chatService.createGroup(groupeDto);
        return new ResponseEntity<>(groupeMapper.toDto(groupe), HttpStatus.CREATED);
    }
}