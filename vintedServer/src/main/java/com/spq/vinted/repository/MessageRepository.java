package com.spq.vinted.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spq.vinted.model.ChatRoom;
import com.spq.vinted.model.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatRoomOrderByTimestampAsc(ChatRoom chatRoom);
}
