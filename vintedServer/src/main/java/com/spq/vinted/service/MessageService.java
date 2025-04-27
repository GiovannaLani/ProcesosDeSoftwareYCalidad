package com.spq.vinted.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spq.vinted.dto.ChatMessage;
import com.spq.vinted.model.ChatRoom;
import com.spq.vinted.model.Item;
import com.spq.vinted.model.Message;
import com.spq.vinted.model.User;
import com.spq.vinted.repository.ChatRoomRepository;
import com.spq.vinted.repository.ItemRepository;
import com.spq.vinted.repository.MessageRepository;
import com.spq.vinted.repository.UserRepository;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    public void sendMessage(long token, long chatRoomId, String content) {
        User sender = userService.getUserByToken(token);

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new RuntimeException("ChatRoom not found"));

        Message message = new Message();
        message.setChatRoom(chatRoom);
        message.setSender(sender);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());

        messageRepository.save(message);
    }

    public List<Message> getMessagesForChatRoom(long chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new RuntimeException("ChatRoom not found"));
        return messageRepository.findByChatRoomOrderByTimestampAsc(chatRoom);
    }

    public Message saveMessage(ChatMessage chatMessage) {
        User sender = userRepository.findById(String.valueOf(chatMessage.getSenderId())).orElseThrow(() -> new RuntimeException("Sender not found"));
        
        ChatRoom chatRoom = chatRoomRepository.findById(chatMessage.getChatRoomId()).orElseThrow(() -> new RuntimeException("ChatRoom not found"));

        Message message = new Message();
        message.setChatRoom(chatRoom);
        message.setSender(sender);
        message.setContent(chatMessage.getContent());
        message.setTimestamp(LocalDateTime.now());

        return messageRepository.save(message);
    }
}
