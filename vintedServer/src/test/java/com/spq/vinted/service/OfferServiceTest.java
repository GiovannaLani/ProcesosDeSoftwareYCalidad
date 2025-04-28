package com.spq.vinted.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.spq.vinted.dto.ItemDTO;
import com.spq.vinted.model.ChatRoom;
import com.spq.vinted.model.Item;
import com.spq.vinted.model.Offer;
import com.spq.vinted.model.User;
import com.spq.vinted.repository.ChatRoomRepository;
import com.spq.vinted.repository.ItemRepository;
import com.spq.vinted.repository.OfferRepository;
import com.spq.vinted.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class OfferServiceTest {

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private ChatRoomRepository chatRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private OfferService offerService;

    private User sender;
    private User receiver;
    private Item item;
    private ChatRoom chatRoom;
    private Offer offer;
    private Long token;

    @BeforeEach
    void setUp() {
        token = 12345L;
        
        sender = new User();
        sender.setId(1L);
        sender.setUsername("sender");

        receiver = new User();
        receiver.setId(2L);
        receiver.setUsername("receiver");

        item = new Item() {
            @Override
            public ItemDTO toDTO() {
                return null;
            }
        };
        item.setId(100L);
        item.setTitle("Test Item");

        chatRoom = new ChatRoom();
        chatRoom.setId(50L);

        offer = new Offer();
        offer.setId(1L);
        offer.setSender(sender);
        offer.setReceiver(receiver);
        offer.setItem(item);
        offer.setChat(chatRoom);
        offer.setPrice(99.99);
        offer.setStatus(Offer.OfferStatus.PENDING);
        offer.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateOffer_Success() {
        // Arrange
        when(userService.getUserByToken(token)).thenReturn(sender);
        when(userRepository.findById("2")).thenReturn(Optional.of(receiver));
        when(itemRepository.findById(100L)).thenReturn(Optional.of(item));
        when(chatRepository.findById(50L)).thenReturn(Optional.of(chatRoom));
        when(offerRepository.save(any(Offer.class))).thenReturn(offer);

        // Act
        Offer result = offerService.createOffer(token, 2L, 100L, 50L, 99.99);

        // Assert
        assertNotNull(result);
        assertEquals(sender, result.getSender());
        assertEquals(receiver, result.getReceiver());
        assertEquals(item, result.getItem());
        assertEquals(chatRoom, result.getChat());
        assertEquals(99.99, result.getPrice());
        assertEquals(Offer.OfferStatus.PENDING, result.getStatus());
        
        verify(offerRepository).save(any(Offer.class));
    }

    @Test
    void testCreateOffer_ReceiverNotFound() {
        // Arrange
        when(userService.getUserByToken(token)).thenReturn(sender);
        when(userRepository.findById("2")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            offerService.createOffer(token, 2L, 100L, 50L, 99.99);
        });
    }

    @Test
    void testCreateOffer_ItemNotFound() {
        // Arrange
        when(userService.getUserByToken(token)).thenReturn(sender);
        when(userRepository.findById("2")).thenReturn(Optional.of(receiver));
        when(itemRepository.findById(100L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            offerService.createOffer(token, 2L, 100L, 50L, 99.99);
        });
    }

    @Test
    void testCreateOffer_ChatRoomNotFound() {
        // Arrange
        when(userService.getUserByToken(token)).thenReturn(sender);
        when(userRepository.findById("2")).thenReturn(Optional.of(receiver));
        when(itemRepository.findById(100L)).thenReturn(Optional.of(item));
        when(chatRepository.findById(50L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            offerService.createOffer(token, 2L, 100L, 50L, 99.99);
        });
    }

    @Test
    void testAcceptOffer_Success() throws Exception {
        // Arrange
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
        when(offerRepository.save(any(Offer.class))).thenReturn(offer);

        // Act
        Offer result = offerService.acceptOffer(1L);

        // Assert
        assertNotNull(result);
        assertEquals(Offer.OfferStatus.ACCEPTED, result.getStatus());
        verify(offerRepository).save(offer);
    }

    @Test
    void testAcceptOffer_AlreadyAccepted() {
        // Arrange
        offer.setStatus(Offer.OfferStatus.ACCEPTED);
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            offerService.acceptOffer(1L);
        });
        assertEquals("This offer is no longer pending", exception.getMessage());
    }

    @Test
    void testAcceptOffer_AlreadyRejected() {
        // Arrange
        offer.setStatus(Offer.OfferStatus.REJECTED);
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            offerService.acceptOffer(1L);
        });
        assertEquals("This offer is no longer pending", exception.getMessage());
    }

    @Test
    void testAcceptOffer_NotFound() {
        // Arrange
        when(offerRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            offerService.acceptOffer(1L);
        });
    }

    @Test
    void testRejectOffer_Success() throws Exception {
        // Arrange
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
        when(offerRepository.save(any(Offer.class))).thenReturn(offer);

        // Act
        Offer result = offerService.rejectOffer(1L);

        // Assert
        assertNotNull(result);
        assertEquals(Offer.OfferStatus.REJECTED, result.getStatus());
        verify(offerRepository).save(offer);
    }

    @Test
    void testRejectOffer_AlreadyAccepted() {
        // Arrange
        offer.setStatus(Offer.OfferStatus.ACCEPTED);
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            offerService.rejectOffer(1L);
        });
        assertEquals("This offer is no longer pending", exception.getMessage());
    }

    @Test
    void testRejectOffer_AlreadyRejected() {
        // Arrange
        offer.setStatus(Offer.OfferStatus.REJECTED);
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            offerService.rejectOffer(1L);
        });
        assertEquals("This offer is no longer pending", exception.getMessage());
    }

    @Test
    void testRejectOffer_NotFound() {
        // Arrange
        when(offerRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            offerService.rejectOffer(1L);
        });
    }

    @Test
    void testGetOffersByItem_Success() {
        // Arrange
        List<Offer> offers = new ArrayList<>();
        offers.add(offer);
        
        when(userService.getUserByToken(token)).thenReturn(sender);
        when(offerRepository.findByItemIdAndSenderId(100L, 1L)).thenReturn(offers);

        // Act
        List<Offer> result = offerService.getOffersByItem(100L, token);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(offer, result.get(0));
    }

    @Test
    void testGetOffersByItem_EmptyList() {
        // Arrange
        when(userService.getUserByToken(token)).thenReturn(sender);
        when(offerRepository.findByItemIdAndSenderId(100L, 1L)).thenReturn(new ArrayList<>());

        // Act
        List<Offer> result = offerService.getOffersByItem(100L, token);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetOfferById_Success() {
        // Arrange
        when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));

        // Act
        Offer result = offerService.getOfferById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(offer, result);
    }

    @Test
    void testGetOfferById_NotFound() {
        // Arrange
        when(offerRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            offerService.getOfferById(1L);
        });
        assertEquals("Offer not found", exception.getMessage());
    }
}