package com.spq.vinted.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.spq.vinted.dto.ChatMessage;
import com.spq.vinted.service.MessageService;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageService messageService;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessage message) {
        
        String destination = "/topic/chat/" + message.getChatRoomId();;
        
        messagingTemplate.convertAndSend(destination, message);
        messageService.saveMessage(message);
    }
}