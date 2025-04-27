package com.spq.client.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.spq.client.data.ChatMessage;
import com.spq.client.data.Offer;
import com.spq.client.data.OfferCreator;
import com.spq.client.data.User;

@RestController
@RequestMapping("/chatrooms")
public class ChatRoomController {

    @Autowired
    private IVintedServiceProxy vintedService;

    @GetMapping("/{chatRoomId}/messages")
    public List<ChatMessage> getMessages(@PathVariable Long chatRoomId) {
        List<ChatMessage> m = vintedService.getMessagesForChatRoom(chatRoomId);
        System.out.println("ChatRoom mesa:" + m);
        return m;
    }
    @GetMapping("/otherUser/{userId}")
    public User getOtherUser(@PathVariable Long userId, @RequestParam Long token) {
        return vintedService.getUser(userId, token);
    }

    @PostMapping("/offers/create")
    public ResponseEntity<Object> createOffer(@RequestBody OfferCreator request, @RequestParam Long token, RedirectAttributes redirectAttributes, Model model) {    
        try {
            Offer createdOffer = vintedService.createOffer(request, token);
            return ResponseEntity.ok(createdOffer);  // 👈 Devolver el Offer al JS
        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.badRequest().body("Error al realizar la oferta: " + e.getMessage());
        }
    }

    @GetMapping("/item/{itemId}/offers")
    public ResponseEntity<List<Offer>> getOffersByItem(@PathVariable Long itemId, @RequestParam Long token) {
        List<Offer> offers = vintedService.getOffersByItem(itemId, token);
        return new ResponseEntity<>(offers, HttpStatus.OK);
    }

    @GetMapping("/offers/{offerId}")
    public ResponseEntity<Offer> getOfferById(@PathVariable Long offerId) {
        Offer offer = vintedService.getOfferById(offerId);
        return new ResponseEntity<>(offer, HttpStatus.OK);
    }

    @PostMapping("/offers/{offerId}/accept")
    public ResponseEntity<Object> acceptOffer(@PathVariable Long offerId) {    
        try {
            vintedService.acceptOffer(offerId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.badRequest().body("Error al aceptar la oferta: " + e.getMessage());
        }
    }
    @PostMapping("/offers/{offerId}/reject")
    public ResponseEntity<Object> rejectOffer(@PathVariable Long offerId) {    
        try {
            vintedService.rejectOffer(offerId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace(); 
            return ResponseEntity.badRequest().body("Error al aceptar la oferta: " + e.getMessage());
        }
    }
}

