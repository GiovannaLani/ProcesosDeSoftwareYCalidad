package com.spq.vinted.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spq.vinted.dto.OfferReturnerDTO;
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

import jakarta.transaction.Transactional;

@Service
public class OfferService {
    
    @Autowired
    private OfferRepository offerRepository;
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private ChatRoomRepository chatRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private MessageService messageService;
    
    @Autowired
    private UserService userService;
    
    
    public Boolean createOffer(Long token, Long receiverId, Long itemId, Long chatId,Double price) {
        User sender = userService.getUserByToken(token);
        User receiver = userRepository.findById(receiverId.toString()).orElseThrow(() -> new NoSuchElementException("Receiver not found"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NoSuchElementException("Item not found"));
        ChatRoom chat = chatRepository.findById(chatId).orElseThrow(() -> new NoSuchElementException("ChatRoom not found"));
        
        Offer offer = new Offer();
        offer.setSender(sender);
        offer.setReceiver(receiver);
        offer.setItem(item);
        offer.setPrice(price);
        offer.setStatus(Offer.OfferStatus.PENDING);
        offer.setChat(chat);
        offer.setCreatedAt(LocalDateTime.now());
        
        offerRepository.save(offer);
        
        return true;
    }



    public List<Offer> getOffersByItem(Long itemId, Long token) {
        User user = userService.getUserByToken(token);
        return offerRepository.findByItemIdAndSenderId(itemId, user.getId());
    }
}