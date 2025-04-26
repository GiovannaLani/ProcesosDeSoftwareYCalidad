package com.spq.client.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spq.client.data.ChatMessage;
import com.spq.client.data.Offer;
import com.spq.client.data.User;

@RestController
@RequestMapping("/chatrooms")
public class ChatRoomController {

    @Autowired
    private IVintedServiceProxy vintedService;

    @GetMapping("/{chatRoomId}/messages")
    public List<ChatMessage> getMessages(@PathVariable Long chatRoomId) {
        return vintedService.getMessagesForChatRoom(chatRoomId);
    }
    @GetMapping("/otherUser/{userId}")
    public User getOtherUser(@PathVariable Long userId, @RequestParam Long token) {
        return vintedService.getUser(userId, token);
    }

    @PostMapping("/create")
    public Map<String, Offer> createOffer(@RequestBody Offer request) {
        return vintedService.createOffer(request);
    }

    @PostMapping("/{id}/accept")
    public Offer acceptOffer(@PathVariable Long id) {
        return vintedService.acceptOffer(id);
    }

    @PostMapping("/{id}/reject")
    public Offer rejectOffer(@PathVariable Long id) {
        return vintedService.rejectOffer(id);
    }

}

