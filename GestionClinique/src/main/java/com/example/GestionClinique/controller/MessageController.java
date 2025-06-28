package com.example.GestionClinique.controller;


import com.example.GestionClinique.controller.controllerApi.MessageApi;
import com.example.GestionClinique.dto.ResponseDto.MessageResponseDto;
import com.example.GestionClinique.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MessageController implements MessageApi {
    private MessageService messageService;

    @Autowired
    MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public MessageResponseDto saveMessage(MessageResponseDto messageResponseDto) {
        return messageService.save(messageResponseDto);
    }

    @Override
    public MessageResponseDto updateMessage(Integer id, MessageResponseDto messageResponseDto) {
        return messageService.updateMessage(id, messageResponseDto);
    }

    @Override
    public MessageResponseDto findMessageById(Integer id) {
        return messageService.findMessageById(id);
    }

    @Override
    public List<MessageResponseDto> findAllMessages() {
        return messageService.findAllMessages();
    }

    @Override
    public void deleteMessageById(Integer id) {
        messageService.deleteMessageById(id);
    }

    @Override
    public List<MessageResponseDto> findMessagesBySenderId(Integer id) {
        return messageService.findMessagesBySenderId(id);
    }

    @Override
    public List<MessageResponseDto> findMessagesByReceiverId(Integer id) {
        return messageService.findMessagesByReceiverId(id);
    }

    @Override
    public MessageResponseDto markMessageAsRead(Integer id) {
        return messageService.markMessageAsRead(id);
    }
}
