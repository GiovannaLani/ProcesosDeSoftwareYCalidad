package com.spq.vinted.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.spq.vinted.dto.ChatMessageDTO;
import com.spq.vinted.model.ChatRoom;
import com.spq.vinted.model.Item;
import com.spq.vinted.model.Message;
import com.spq.vinted.model.Offer;
import com.spq.vinted.model.User;
import com.spq.vinted.repository.ChatRoomRepository;
import com.spq.vinted.repository.MessageRepository;
import com.spq.vinted.repository.UserRepository;

class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendMessage() {
        User sender = new User();
        ChatRoom chatRoom = new ChatRoom();

        when(userService.getUserByToken(1L)).thenReturn(sender);
        when(chatRoomRepository.findById(2L)).thenReturn(Optional.of(chatRoom));

        messageService.sendMessage(1L, 2L, "Hello World");

        verify(messageRepository).save(any(Message.class));
    }

    @Test
    void testSendMessage_ChatRoomNotFound() {
        when(userService.getUserByToken(1L)).thenReturn(new User());
        when(chatRoomRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            messageService.sendMessage(1L, 2L, "Hello");
        });

        assertEquals("ChatRoom not found", exception.getMessage());
    }

    @Test
    void testGetMessagesForChatRoom() {
        ChatRoom chatRoom = new ChatRoom();
        Message m1 = new Message();
        Message m2 = new Message();

        when(chatRoomRepository.findById(2L)).thenReturn(Optional.of(chatRoom));
        when(messageRepository.findByChatRoomOrderByTimestampAsc(chatRoom)).thenReturn(Arrays.asList(m1, m2));

        List<Message> messages = messageService.getMessagesForChatRoom(2L);

        assertEquals(2, messages.size());
    }

    @Test
    void testGetMessagesForChatRoom_NotFound() {
        when(chatRoomRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            messageService.getMessagesForChatRoom(2L);
        });

        assertEquals("ChatRoom not found", exception.getMessage());
    }

/*     @Test
    void testSaveMessage() {
        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
        chatMessageDTO.setSenderId(1L);
        chatMessageDTO.setChatRoomId(2L);
        chatMessageDTO.setContent("Hola!");

        User sender = new User();
        ChatRoom chatRoom = new ChatRoom();

        when(userRepository.findById("1")).thenReturn(Optional.of(sender));
        when(chatRoomRepository.findById(2L)).thenReturn(Optional.of(chatRoom));

        Message result = messageService.saveMessage(chatMessageDTO);

        assertNotNull(result);
        verify(messageRepository).save(any(Message.class));
    }  */

    @Test
    void testSaveMessage_SenderNotFound() {
        when(userRepository.findById("1")).thenReturn(Optional.empty());

        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
        chatMessageDTO.setSenderId(1L);
        chatMessageDTO.setChatRoomId(2L);
        chatMessageDTO.setContent("Hola!");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            messageService.saveMessage(chatMessageDTO);
        });

        assertEquals("Sender not found", exception.getMessage());
    }

    @Test
    void testSaveMessage_ChatRoomNotFound() {
        when(userRepository.findById("1")).thenReturn(Optional.of(new User()));
        when(chatRoomRepository.findById(2L)).thenReturn(Optional.empty());

        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
        chatMessageDTO.setSenderId(1L);
        chatMessageDTO.setChatRoomId(2L);
        chatMessageDTO.setContent("Hola!");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            messageService.saveMessage(chatMessageDTO);
        });

        assertEquals("ChatRoom not found", exception.getMessage());
    }

    @Test
    void testSaveAndSendMessage() {
        Message message = new Message();
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(2L);
        message.setChatRoom(chatRoom);
        message.setSender(new User());
        message.setContent("Test message");

        when(messageRepository.save(message)).thenReturn(message);

        Message result = messageService.saveAndSendMessage(message);

        assertNotNull(result);
        verify(messagingTemplate).convertAndSend(eq("/topic/chat/2"), any(ChatMessageDTO.class));
    }
/* 
    @Test
    void testSaveAndSendMessage_WithOffer() {
        Message message = new Message();
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(2L);
        message.setChatRoom(chatRoom);

        User sender = new User();
        sender.setId(1L);
        User receiver = new User();
        receiver.setId(2L);
        Item item = mock(Item.class);
        item.setId(3L);

        Offer offer = new Offer();
        offer.setPrice(50.0);
        offer.setSender(sender);
        offer.setReceiver(receiver);
        offer.setItem(item);
        offer.setStatus(Offer.OfferStatus.PENDING);

        message.setSender(sender);
        message.setContent("Oferta");
        message.setOffer(offer);
        message.setType(Message.MessageType.OFFER);

        when(messageRepository.save(message)).thenReturn(message);

        Message result = messageService.saveAndSendMessage(message);

        assertNotNull(result);
        verify(messagingTemplate).convertAndSend(eq("/topic/chat/2"), any(ChatMessageDTO.class));
    } */

}
