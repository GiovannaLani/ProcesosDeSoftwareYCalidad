package com.spq.vinted.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChatRoomTest {

    private ChatRoom chatRoom;
    private Item item;
    private User buyer;
    private User seller;

    @BeforeEach
    void setUp() {
        item = new Clothes();
        item.setTitle("Test Item");

        buyer = new User("buyer@example.com", "password123", "buyerUser", "Buyer", "User");
        seller = new User("seller@example.com", "password456", "sellerUser", "Seller", "User");

        chatRoom = new ChatRoom(item, buyer, seller);
    }

    @Test
    void testConstructor() {
        assertEquals(item, chatRoom.getItem(), "El ítem debería coincidir");
        assertEquals(buyer, chatRoom.getBuyer(), "El comprador debería coincidir");
        assertEquals(seller, chatRoom.getSeller(), "El vendedor debería coincidir");
        assertNotNull(chatRoom.getMessages(), "La lista de mensajes no debería ser nula");
        assertTrue(chatRoom.getMessages().isEmpty(), "La lista de mensajes debería estar vacía inicialmente");
    }

    @Test
    void testSettersAndGetters() {
        Item newItem = new Home();
        newItem.setTitle("New Item");

        User newBuyer = new User("newbuyer@example.com", "password789", "newBuyerUser", "NewBuyer", "User");
        User newSeller = new User("newseller@example.com", "password987", "newSellerUser", "NewSeller", "User");

        chatRoom.setItem(newItem);
        chatRoom.setBuyer(newBuyer);
        chatRoom.setSeller(newSeller);

        assertEquals(newItem, chatRoom.getItem(), "El nuevo ítem debería coincidir");
        assertEquals(newBuyer, chatRoom.getBuyer(), "El nuevo comprador debería coincidir");
        assertEquals(newSeller, chatRoom.getSeller(), "El nuevo vendedor debería coincidir");
    }

    @Test
    void testAddMessages() {
        Message message1 = new Message();
        message1.setContent("Hola, ¿está disponible?");
        message1.setSender(buyer);

        Message message2 = new Message();
        message2.setContent("Sí, está disponible.");
        message2.setSender(seller);

        List<Message> messages = new ArrayList<>();
        messages.add(message1);
        messages.add(message2);

        chatRoom.setMessages(messages);

        assertEquals(2, chatRoom.getMessages().size(), "La lista de mensajes debería contener 2 mensajes");
        assertEquals("Hola, ¿está disponible?", chatRoom.getMessages().get(0).getContent(), "El contenido del primer mensaje debería coincidir");
        assertEquals("Sí, está disponible.", chatRoom.getMessages().get(1).getContent(), "El contenido del segundo mensaje debería coincidir");
    }

    @Test
    void testEmptyMessages() {
        chatRoom.setMessages(new ArrayList<>());
        assertTrue(chatRoom.getMessages().isEmpty(), "La lista de mensajes debería estar vacía");
    }
}