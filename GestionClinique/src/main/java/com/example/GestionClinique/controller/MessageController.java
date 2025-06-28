package com.example.GestionClinique.controller;


import com.example.GestionClinique.controller.controllerApi.MessageApi;
import com.example.GestionClinique.dto.MessageDto;
import com.example.GestionClinique.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
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
    public MessageDto saveMessage(MessageDto messageDto) {
        return messageService.save(messageDto);
    }

    @Override
    public MessageDto updateMessage(Integer id, MessageDto messageDto) {
        return messageService.updateMessage(id, messageDto);
    }

    @Override
    public MessageDto findMessageById(Integer id) {
        return messageService.findMessageById(id);
    }

    @Override
    public List<MessageDto> findAllMessages() {
        return messageService.findAllMessages();
    }

    @Override
    public void deleteMessageById(Integer id) {
        messageService.deleteMessageById(id);
    }

    @Override
    public List<MessageDto> findMessagesBySenderId(Integer id) {
        return messageService.findMessagesBySenderId(id);
    }

    @Override
    public List<MessageDto> findMessagesByReceiverId(Integer id) {
        return messageService.findMessagesByReceiverId(id);
    }

    @Override
    public MessageDto markMessageAsRead(Integer id) {
        return messageService.markMessageAsRead(id);
    }
}
