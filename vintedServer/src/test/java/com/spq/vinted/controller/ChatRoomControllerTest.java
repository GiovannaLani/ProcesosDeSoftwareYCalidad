package com.spq.vinted.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spq.vinted.dto.ChatMessageDTO;
import com.spq.vinted.dto.ChatRoomDTO;
import com.spq.vinted.model.ChatRoom;
import com.spq.vinted.model.Item;
import com.spq.vinted.model.Message;
import com.spq.vinted.model.User;
import com.spq.vinted.service.ChatRoomService;
import com.spq.vinted.service.MessageService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatRoomController.class)
class ChatRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatRoomService chatRoomService;

    @MockBean
    private MessageService messageService;

    @Autowired
    private ObjectMapper objectMapper;

    private ChatRoom chatRoom;
    private User buyer;
    private User seller;
    private Item item;

    @BeforeEach
    void setUp() {
        buyer = new User();
        buyer.setId(1L);
        buyer.setUsername("buyerUser");

        seller = new User();
        seller.setId(2L);
        seller.setUsername("sellerUser");

        item = new Item() {
            @Override
            public com.spq.vinted.dto.ItemDTO toDTO() {
                return null;
            }
        };
        item.setId(3L);
        item.setTitle("Cool Jacket");
        item.setPrice(50.0f);
        item.setImages(List.of("image.jpg"));

        chatRoom = new ChatRoom();
        chatRoom.setId(100L);
        chatRoom.setBuyer(buyer);
        chatRoom.setSeller(seller);
        chatRoom.setItem(item);
    }

    @Test
    void testCreateChatRoom() throws Exception {
        ChatRoomDTO chatRoomDTO = new ChatRoomDTO(1L, 2L, 3L);

        Mockito.when(chatRoomService.getOrCreateChatRoom(1L, 2L, 3L))
                .thenReturn(chatRoom);

        mockMvc.perform(post("/chatrooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chatRoomDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100));
    }

    @Test
    void testGetChatRoomsForUser() throws Exception {
        Mockito.when(chatRoomService.getChatRoomsForUser(1L))
                .thenReturn(List.of(chatRoom));
    
        mockMvc.perform(get("/chatrooms/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].buyerName").value("buyerUser"))
                .andExpect(jsonPath("$[0].sellerName").value("sellerUser"))
                .andExpect(jsonPath("$[0].itemName").value("Cool Jacket"));
    }

    @Test
    void testGetMessages() throws Exception {
        Message message = new Message();
        message.setContent("Hello there!");
        message.setTimestamp(LocalDateTime.now());
        message.setSender(buyer);
        message.setChatRoom(chatRoom);

        Mockito.when(messageService.getMessagesForChatRoom(100L))
                .thenReturn(List.of(message));

        mockMvc.perform(get("/chatrooms/100/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].content").value("Hello there!"))
                .andExpect(jsonPath("$[0].senderId").value(1));
    }

}
