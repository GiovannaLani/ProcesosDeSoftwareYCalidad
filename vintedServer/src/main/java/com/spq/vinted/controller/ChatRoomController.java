package com.spq.vinted.controller;

import com.spq.vinted.dto.ChatMessage;
import com.spq.vinted.dto.ChatRoomDTO;
import com.spq.vinted.dto.ChatRoomInfoDTO;
import com.spq.vinted.model.ChatRoom;
import com.spq.vinted.model.Message;
import com.spq.vinted.service.ChatRoomService;
import com.spq.vinted.service.MessageService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chatrooms")
public class ChatRoomController {

    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private MessageService messageService;

    @PostMapping
    public ResponseEntity<ChatRoom> createChatRoom(
            @RequestBody ChatRoomDTO chatRoomDTO) {

        ChatRoom chatRoom = chatRoomService.getOrCreateChatRoom(chatRoomDTO.getBuyerId(), chatRoomDTO.getSellerId(), chatRoomDTO.getItemId());

        return ResponseEntity.ok(chatRoom);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ChatRoomInfoDTO>> getChatRoomsForUser(@PathVariable Long userId) {
        List<ChatRoomInfoDTO> chatRooms = chatRoomService.getChatRoomsForUser(userId).stream().map(chatRoom -> 
            new ChatRoomInfoDTO(
                chatRoom.getId(),
                chatRoom.getBuyer().getId(),
                chatRoom.getBuyer().getUsername(),
                chatRoom.getSeller().getId(),
                chatRoom.getSeller().getUsername(),
                chatRoom.getItem().getId(),
                chatRoom.getItem().getTitle(),
                chatRoom.getItem().getImages().get(0),
                chatRoom.getItem().getPrice()
            )
        ).toList();
        return ResponseEntity.ok(chatRooms);
    }

    @GetMapping("/{chatRoomId}/messages")
    public List<ChatMessage> getMessages(@PathVariable long chatRoomId) {
        List<Message> messages = messageService.getMessagesForChatRoom(chatRoomId);
        return messages.stream().map(message -> new ChatMessage(
            message.getContent(),
            message.getChatRoom().getId(),
            message.getSender().getId(),
            message.getTimestamp()
        )).toList();
    }

}
