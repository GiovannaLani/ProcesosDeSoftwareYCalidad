package com.spq.vinted.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    
    
    public Offer createOffer(Long senderId, Long receiverId, Long itemId, Long chatId, Double price) {
        User sender = userRepository.findById(senderId.toString()).orElseThrow();
        User receiver = userRepository.findById(receiverId.toString()).orElseThrow();
        Item item = itemRepository.findById(itemId).orElseThrow();
        ChatRoom chat = chatRepository.findById(chatId).orElseThrow();
        
        Offer offer = new Offer();
        offer.setSender(sender);
        offer.setReceiver(receiver);
        offer.setItem(item);
        offer.setPrice(price);
        offer.setStatus(Offer.OfferStatus.PENDING);
        offer.setChat(chat);
        offer.setCreatedAt(LocalDateTime.now());
        
        offerRepository.save(offer);
        
        Message message = new Message();
        message.setSender(sender);
        message.setChatRoom(chat);
        message.setOffer(offer);
        message.setTimestamp(LocalDateTime.now());
        
        messageRepository.save(message);
        
        return offer;
    }
    

    public Offer acceptOffer(Long offerId, Long userId) throws Exception {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow();
        
        if (!offer.getReceiver().getId().equals(userId)) {
            throw new Exception("This offer is no longer pending");
        }
        
        if (offer.getStatus() != Offer.OfferStatus.PENDING) {
            throw new Exception("This offer is no longer pending");
        }
        
        offer.setStatus(Offer.OfferStatus.ACCEPTED);
        offerRepository.save(offer);

        Message offerMessage = messageRepository.findByOfferId(offerId);
        if (offerMessage != null) {
            messageService.saveAndSendMessage(offerMessage);
        }
        
        return offer;
    }
    

   
    public Offer rejectOffer(Long offerId, Long userId) throws Exception {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow();
        
        if (!offer.getReceiver().getId().equals(userId)) {
            throw new Exception("Only the receiver can reject the offer");
        }
        
        if (offer.getStatus() != Offer.OfferStatus.PENDING) {
            throw new Exception("This offer is no longer pending");
        }
        
        offer.setStatus(Offer.OfferStatus.REJECTED);
        offerRepository.save(offer);

        Message offerMessage = messageRepository.findByOfferId(offerId);
        if (offerMessage != null) {
            messageService.saveAndSendMessage(offerMessage);
        }
        
        return offer;
    }
}