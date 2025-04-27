package com.spq.vinted.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spq.vinted.dto.OfferDTO;
import com.spq.vinted.model.ChatRoom;
import com.spq.vinted.model.Item;
import com.spq.vinted.model.Offer;
import com.spq.vinted.model.User;
import com.spq.vinted.service.OfferService;

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

import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OfferController.class)
class OfferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OfferService offerService;

    @Autowired
    private ObjectMapper objectMapper;

    private Offer offer;
    private User sender;
    private User receiver;
    private Item item;
    private ChatRoom chatRoom;

    @BeforeEach
    void setUp() {
        sender = new User();
        sender.setId(1L);

        receiver = new User();
        receiver.setId(2L);

        item = new Item() {
            @Override
            public com.spq.vinted.dto.ItemDTO toDTO() {
                return null;
            }
        };
        item.setId(3L);
        item.setPrice(100.0f);

        chatRoom = new ChatRoom();
        chatRoom.setId(4L);

        offer = new Offer();
        offer.setId(5L);
        offer.setPrice(80.0);
        offer.setItem(item);
        offer.setSender(sender);
        offer.setReceiver(receiver);
        offer.setChat(chatRoom);
        offer.setStatus(Offer.OfferStatus.PENDING);    }

    @Test
    void testCreateOffer() throws Exception {
        OfferDTO offerDTO = new OfferDTO(2.0,"hola", 3L, 4L, 8L, 5L);

        Mockito.when(offerService.createOffer(anyLong(), anyLong(), anyLong(), anyLong(), any()))
                .thenReturn(offer);

        mockMvc.perform(post("/offers/create")
                .param("token", "123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(offerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.price").value(80.0));
    }

    @Test
    void testAcceptOffer() throws Exception {
        Mockito.when(offerService.acceptOffer(5L)).thenReturn(offer);

        mockMvc.perform(put("/offers/5/accept"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING")); // El estado de oferta depende de cómo mockees
    }

    @Test
    void testAcceptOffer_Exception() throws Exception {
        Mockito.when(offerService.acceptOffer(5L))
                .thenThrow(new RuntimeException("Error"));

        mockMvc.perform(put("/offers/5/accept"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRejectOffer() throws Exception {
        Mockito.when(offerService.rejectOffer(5L)).thenReturn(offer);

        mockMvc.perform(put("/offers/5/reject"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING")); // Depende del estado seteado
    }

    @Test
    void testRejectOffer_Exception() throws Exception {
        Mockito.when(offerService.rejectOffer(5L))
                .thenThrow(new RuntimeException("Error"));

        mockMvc.perform(put("/offers/5/reject"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetOffersByItem() throws Exception {
        Mockito.when(offerService.getOffersByItem(3L, 123L))
                .thenReturn(List.of(offer));

        mockMvc.perform(get("/offers/item/3")
                .param("token", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].price").value(80.0));
    }

    @Test
    void testGetOfferById() throws Exception {
        Mockito.when(offerService.getOfferById(5L))
                .thenReturn(offer);

        mockMvc.perform(get("/offers/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.price").value(80.0));
    }

    @Test
    void testGetOfferById_NotFound() throws Exception {
        Mockito.when(offerService.getOfferById(5L))
                .thenReturn(null);

        mockMvc.perform(get("/offers/5"))
                .andExpect(status().isNotFound());
    }

}
