package com.spq.vinted.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spq.vinted.model.ChatRoom;
import com.spq.vinted.model.Item;
import com.spq.vinted.model.User;
import com.spq.vinted.repository.ChatRoomRepository;
import com.spq.vinted.repository.ItemRepository;
import com.spq.vinted.repository.UserRepository;

@Service
public class ChatRoomService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    public ChatRoom getOrCreateChatRoom(long buyerId, long sellerId, long itemId) {
        User buyer = userRepository.findById(String.valueOf(buyerId)).orElseThrow(() -> new RuntimeException("Buyer not found"));
        User seller = userRepository.findById(String.valueOf(sellerId)).orElseThrow(() -> new RuntimeException("Seller not found"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new RuntimeException("Item not found"));

        Optional<ChatRoom> existingChat = chatRoomRepository.findByItemAndBuyerAndSeller(item, buyer, seller);
        if (existingChat.isPresent()) {
            return existingChat.get();
        }

        ChatRoom newChat = new ChatRoom();
        newChat.setBuyer(buyer);
        newChat.setSeller(seller);
        newChat.setItem(item);

        return chatRoomRepository.save(newChat);
    }

    public List<ChatRoom> getChatRoomsForUser(Long userId) {
        return chatRoomRepository.findByBuyerIdOrSellerId(userId, userId);
    }
}