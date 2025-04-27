package com.spq.vinted.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.spq.vinted.model.ChatRoom;
import com.spq.vinted.model.Item;
import com.spq.vinted.model.User;
import com.spq.vinted.repository.ChatRoomRepository;
import com.spq.vinted.repository.ItemRepository;
import com.spq.vinted.repository.UserRepository;

class ChatRoomServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ChatRoomService chatRoomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetOrCreateChatRoom_CreatesNewChat() {
        User buyer = new User();
        User seller = new User();
        Item item = mock(Item.class);

        when(userRepository.findById("1")).thenReturn(Optional.of(buyer));
        when(userRepository.findById("2")).thenReturn(Optional.of(seller));
        when(itemRepository.findById(3L)).thenReturn(Optional.of(item));
        when(chatRoomRepository.findByItemAndBuyerAndSeller(item, buyer, seller)).thenReturn(Optional.empty());

        ChatRoom newChat = new ChatRoom();
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(newChat);

        ChatRoom result = chatRoomService.getOrCreateChatRoom(1L, 2L, 3L);

        assertNotNull(result);
        verify(chatRoomRepository).save(any(ChatRoom.class));
    }

    @Test
    void testGetOrCreateChatRoom_ReturnsExistingChat() {
        User buyer = new User();
        User seller = new User();
        Item item = mock(Item.class);
        ChatRoom existingChat = new ChatRoom();

        when(userRepository.findById("1")).thenReturn(Optional.of(buyer));
        when(userRepository.findById("2")).thenReturn(Optional.of(seller));
        when(itemRepository.findById(3L)).thenReturn(Optional.of(item));
        when(chatRoomRepository.findByItemAndBuyerAndSeller(item, buyer, seller)).thenReturn(Optional.of(existingChat));

        ChatRoom result = chatRoomService.getOrCreateChatRoom(1L, 2L, 3L);

        assertEquals(existingChat, result);
        verify(chatRoomRepository, never()).save(any(ChatRoom.class));
    }

    @Test
    void testGetOrCreateChatRoom_BuyerNotFound() {
        when(userRepository.findById("1")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            chatRoomService.getOrCreateChatRoom(1L, 2L, 3L);
        });

        assertEquals("Buyer not found", exception.getMessage());
    }

    @Test
    void testGetOrCreateChatRoom_SellerNotFound() {
        when(userRepository.findById("1")).thenReturn(Optional.of(new User()));
        when(userRepository.findById("2")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            chatRoomService.getOrCreateChatRoom(1L, 2L, 3L);
        });

        assertEquals("Seller not found", exception.getMessage());
    }

    
    @Test
    void testGetOrCreateChatRoom_ItemNotFound() {
        when(userRepository.findById("1")).thenReturn(Optional.of(new User()));
        when(userRepository.findById("2")).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(3L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            chatRoomService.getOrCreateChatRoom(1L, 2L, 3L);
        });

        assertEquals("Item not found", exception.getMessage());
    }

    @Test
    void testGetChatRoomsForUser() {
        ChatRoom chat1 = new ChatRoom();
        ChatRoom chat2 = new ChatRoom();

        when(chatRoomRepository.findByBuyerIdOrSellerId(1L, 1L)).thenReturn(Arrays.asList(chat1, chat2));

        List<ChatRoom> result = chatRoomService.getChatRoomsForUser(1L);

        assertEquals(2, result.size());
    }
}