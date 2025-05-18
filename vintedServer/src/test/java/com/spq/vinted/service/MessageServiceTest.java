package com.spq.vinted.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import com.spq.vinted.dto.ChatMessageDTO;
import com.spq.vinted.dto.ClothesDTO;
import com.spq.vinted.dto.ItemDTO;
import com.spq.vinted.model.ChatRoom;
import com.spq.vinted.model.Clothes;
import com.spq.vinted.model.Item;
import com.spq.vinted.model.Message;
import com.spq.vinted.model.Offer;
import com.spq.vinted.model.Offer.OfferStatus;
import com.spq.vinted.model.User;
import com.spq.vinted.repository.ChatRoomRepository;
import com.spq.vinted.repository.MessageRepository;
import com.spq.vinted.repository.OfferRepository;
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

    @Mock
    private OfferRepository offerRepository;

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

    // @Test
    // void testSaveMessage_SenderNotFound() {
    //     when(userRepository.findById("1")).thenReturn(Optional.empty());

    //     ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
    //     chatMessageDTO.setSenderId(1L);
    //     chatMessageDTO.setChatRoomId(2L);
    //     chatMessageDTO.setContent("Hola!");

    //     RuntimeException exception = assertThrows(RuntimeException.class, () -> {
    //         messageService.saveMessage(chatMessageDTO);
    //     });

    //     assertEquals("Sender not found", exception.getMessage());
    // }

    // @Test
    // void testSaveMessage_ChatRoomNotFound() {
    //     when(userRepository.findById("1")).thenReturn(Optional.of(new User()));
    //     when(chatRoomRepository.findById(2L)).thenReturn(Optional.empty());

    //     ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
    //     chatMessageDTO.setSenderId(1L);
    //     chatMessageDTO.setChatRoomId(2L);
    //     chatMessageDTO.setContent("Hola!");

    //     RuntimeException exception = assertThrows(RuntimeException.class, () -> {
    //         messageService.saveMessage(chatMessageDTO);
    //     });

    //     assertEquals("ChatRoom not found", exception.getMessage());
    // }

    @Test
    public void testSaveMessage_WithOffer() {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setSenderId(1L);
        dto.setChatRoomId(2L);
        dto.setContent("10"); // offerId as string
        dto.setType("OFFER");

        User sender = new User();
        sender.setId(1L);

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(2L);

        Offer offer = new Offer();
        offer.setId(10L);

        Message savedMessage = new Message();
        savedMessage.setId(100L);

        // Mocks
        when(userRepository.findById("1")).thenReturn(Optional.of(sender));
        when(chatRoomRepository.findById(2L)).thenReturn(Optional.of(chatRoom));
        when(offerRepository.findById(10L)).thenReturn(Optional.of(offer));
        when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);

        Message result = messageService.saveMessage(dto);

        assertEquals(savedMessage, result);
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    public void testSaveMessage_WithoutOffer() {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setSenderId(1L);
        dto.setChatRoomId(2L);
        dto.setContent("Just a text");
        dto.setType("TEXT");

        User sender = new User();
        sender.setId(1L);

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(2L);

        Message savedMessage = new Message();
        savedMessage.setId(101L);

        when(userRepository.findById("1")).thenReturn(Optional.of(sender));
        when(chatRoomRepository.findById(2L)).thenReturn(Optional.of(chatRoom));
        when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);

        Message result = messageService.saveMessage(dto);

        assertEquals(savedMessage, result);
        verify(messageRepository).save(any(Message.class));
    }

    @Test
    public void testSaveMessage_SenderNotFound_ShouldThrow() {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setSenderId(999L);
        dto.setChatRoomId(2L);

        when(userRepository.findById("999")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            messageService.saveMessage(dto);
        });
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

    @Test
    public void testConvertToDTO_WithOffer() {
        Message message = new Message();
        message.setContent("offer id");
        message.setTimestamp(LocalDateTime.now());
        message.setType(Message.MessageType.OFFER);

        User sender = new User();
        sender.setId(1L);
        message.setSender(sender);

        Offer offer = new Offer();
        offer.setPrice(99.99);
        offer.setStatus(OfferStatus.ACCEPTED);

        User offerSender = new User(); offerSender.setId(2L);
        User offerReceiver = new User(); offerReceiver.setId(3L);

        offer.setSender(offerSender);
        offer.setReceiver(offerReceiver);

        Item item = new Clothes(); item.setId(100L);
        offer.setItem(item);

        message.setOffer(offer);

        // Mock estático si se requiere (si `ItemService.getDTOById()` es estático)
        ClothesDTO itemDTO = new ClothesDTO(); itemDTO.setId(100L);
        try (MockedStatic<ItemService> mocked = mockStatic(ItemService.class)) {
            mocked.when(() -> ItemService.getDTOById(100L)).thenReturn(itemDTO);

            ChatMessageDTO dto = ReflectionTestUtils.invokeMethod(messageService, "convertToDTO", message);

            assertEquals(1L, dto.getSenderId());
            assertEquals(99.99, dto.getOffer().getPrice());
            assertEquals("ACCEPTED", dto.getOffer().getStatus());
            assertEquals(2L, dto.getOffer().getSenderId());
            assertEquals(3L, dto.getOffer().getReceiverId());
            assertEquals(100L, dto.getOffer().getItemId());
        }
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
