package com.spq.vinted.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;
    
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver;
    
    @ManyToOne
    @JoinColumn(name = "Item_id")
    private Item item;
    
    @Column(nullable = false)
    private Double price;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OfferStatus status;
    
    @ManyToOne
    @JoinColumn(name = "chat_id")
    private ChatRoom chat;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    public enum OfferStatus {
        PENDING, ACCEPTED, REJECTED
    }
    
    
    public Offer() {
    }

    public Offer(User sender, User receiver, Item item, Double price, OfferStatus status, ChatRoom chat) {
        this.sender = sender;
        this.receiver = receiver;
        this.item = item;
        this.price = price;
        this.status = status;
        this.chat = chat;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }


    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Double getPrice() {
        return price;
    }


    public void setPrice(Double price) {
        this.price = price;
    }

    public OfferStatus getStatus() {
        return status;
    }

    public void setStatus(OfferStatus status) {
        this.status = status;
    }

    public ChatRoom getChat() {
        return chat;
    }
    public void setChat(ChatRoom chat) {
        this.chat = chat;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }





}
