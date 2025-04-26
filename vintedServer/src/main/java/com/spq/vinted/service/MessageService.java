package com.spq.vinted.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.spq.vinted.dto.ChatMessageDTO;
import com.spq.vinted.dto.ItemDTO;
import com.spq.vinted.dto.OfferDTO;
import com.spq.vinted.model.ChatRoom;
import com.spq.vinted.model.Message;
import com.spq.vinted.model.User;
import com.spq.vinted.repository.ChatRoomRepository;
import com.spq.vinted.repository.ItemRepository;
import com.spq.vinted.repository.MessageRepository;
import com.spq.vinted.repository.UserRepository;

import jakarta.transaction.Transactional;

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
    private SimpMessagingTemplate messagingTemplate;

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

    public Message saveMessage(ChatMessageDTO chatMessage) {
        User sender = userRepository.findById(String.valueOf(chatMessage.getSenderId())).orElseThrow(() -> new RuntimeException("Sender not found"));
        
        ChatRoom chatRoom = chatRoomRepository.findById(chatMessage.getChatRoomId()).orElseThrow(() -> new RuntimeException("ChatRoom not found"));

        Message message = new Message();
        message.setChatRoom(chatRoom);
        message.setSender(sender);
        message.setContent(chatMessage.getContent());
        message.setTimestamp(LocalDateTime.now());

        return messageRepository.save(message);
    }

    @Transactional
    public Message saveAndSendMessage(Message message) {
        Message savedMessage = messageRepository.save(message);
        
        ChatMessageDTO messageDTO = convertToDTO(savedMessage);
        
        messagingTemplate.convertAndSend("/topic/chat/" + message.getChatRoom().getId(), messageDTO);
        
        return savedMessage;
    }
    
    private ChatMessageDTO convertToDTO(Message message) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setSenderId(message.getSender().getId());
        dto.setContent(message.getContent());
        dto.setTimestamp(message.getTimestamp());
        
        if (message.getType() == Message.MessageType.OFFER && message.getOffer() != null) {
            OfferDTO offerDTO = new OfferDTO();
            offerDTO.setId(message.getOffer().getId());
            offerDTO.setPrice(message.getOffer().getPrice());
            offerDTO.setStatus(message.getOffer().getStatus().toString());
            offerDTO.setSenderId(message.getOffer().getSender().getId());
            offerDTO.setReceiverId(message.getOffer().getReceiver().getId());
            
            ItemDTO itemDTO = ItemService.getDTOById(message.getOffer().getItem().getId());
            offerDTO.setItem(itemDTO); 
            

            dto.setOffer(offerDTO);
        }

        return dto;

    }
}
