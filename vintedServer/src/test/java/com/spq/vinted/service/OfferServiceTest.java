package com.spq.vinted.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.spq.vinted.model.ChatRoom;
import com.spq.vinted.model.Item;
import com.spq.vinted.model.Message;
import com.spq.vinted.model.Offer;
import com.spq.vinted.model.User;
import com.spq.vinted.repository.ChatRoomRepository;
import com.spq.vinted.repository.ItemRepository;
import com.spq.vinted.repository.MessageRepository;
import com.spq.vinted.repository.OfferRepository;
import com.spq.vinted.repository.UserRepository;

class OfferServiceTest {

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChatRoomRepository chatRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @Mock
    private MessageService messageService;

    @InjectMocks
    private OfferService offerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOffer() {
        User sender = new User();
        sender.setId(1L);

        User receiver = new User();
        receiver.setId(2L);

        Item item = mock(Item.class);
        item.setId(3L);

        ChatRoom chat = new ChatRoom();
        chat.setId(4L);

        when(userService.getUserByToken(1L)).thenReturn(sender);
        when(userRepository.findById("2")).thenReturn(Optional.of(receiver));
        when(itemRepository.findById(3L)).thenReturn(Optional.of(item));
        when(chatRepository.findById(4L)).thenReturn(Optional.of(chat));

        Offer result = offerService.createOffer(1L, 2L, 3L, 4L, 100.0);
        
        assertNotNull(result);
        assertEquals(sender, result.getSender());   
        verify(offerRepository).save(any(Offer.class));
    }

    @Test
    void testCreateOffer_UserNotFound() {
        when(userService.getUserByToken(1L)).thenReturn(new User());
        when(userRepository.findById("2")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            offerService.createOffer(1L, 2L, 3L, 4L, 100.0);
        });
    }



    @Test
    void testCreateOffer_ItemNotFound() {
        when(userService.getUserByToken(1L)).thenReturn(new User());
        when(userRepository.findById("2")).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            offerService.createOffer(1L, 2L, 3L, 4L, 100.0);
        });
    }


    @Test
    void testCreateOffer_ChatNotFound() {
        when(userService.getUserByToken(1L)).thenReturn(new User());
        when(userRepository.findById("2")).thenReturn(Optional.of(new User()));
        Item mockItem = mock(Item.class);
        when(itemRepository.findById(3L)).thenReturn(Optional.of(mockItem));
        when(chatRepository.findById(4L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            offerService.createOffer(1L, 2L, 3L, 4L, 100.0);
        });
    }


    @Test
    void testGetOffersByItem() {
        User user = new User();
        user.setId(1L);

        Offer offer1 = new Offer();
        Offer offer2 = new Offer();

        when(userService.getUserByToken(1L)).thenReturn(user);
        when(offerRepository.findByItemIdAndSenderId(10L, 1L)).thenReturn(Arrays.asList(offer1, offer2));

        List<Offer> result = offerService.getOffersByItem(10L, 1L);

        assertEquals(2, result.size());
    }

}
