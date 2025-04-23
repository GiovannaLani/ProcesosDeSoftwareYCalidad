package com.spq.vinted.dto;

import java.time.LocalDateTime;

public class ChatMessage {
    private String content;
    private long chatRoomId;
    private long senderId;
    private LocalDateTime timestamp;

    public ChatMessage() {
    }

    public ChatMessage(String content, long chatRoomId, long senderId, LocalDateTime timestamp) {
        this.content = content;
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public long getChatRoomId() {
        return chatRoomId;
    }
    public void setChatRoomId(long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }
    public long getSenderId() {
        return senderId;
    }
    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
