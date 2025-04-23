package com.spq.vinted.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spq.vinted.model.ChatRoom;
import com.spq.vinted.model.Item;
import com.spq.vinted.model.User;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByItemAndBuyerAndSeller(Item item, User buyer, User seller);
    List<ChatRoom> findByBuyerIdOrSellerId(Long buyerId, Long sellerId);
}
