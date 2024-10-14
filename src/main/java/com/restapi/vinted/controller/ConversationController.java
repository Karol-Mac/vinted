package com.restapi.vinted.controller;

import com.restapi.vinted.payload.ConversationDto;
import com.restapi.vinted.payload.MessageDto;
import com.restapi.vinted.service.ConversationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ConversationController {
    private final ConversationService conversationService;


    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @PostMapping("/conversations")
    public ResponseEntity<Void> startConversation(@RequestParam long clotheId, Principal principal) {

        conversationService.startConversation(clotheId, principal.getName());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/conversations/buying")
    public ResponseEntity<List<ConversationDto>> getConversationsBuying(Principal principal) {
        return ResponseEntity.ok(conversationService.getConversationsBuying(principal.getName()));
    }

    @GetMapping("/conversations/selling")
    public ResponseEntity<List<ConversationDto>> getConversations(@RequestParam long clotheId, Principal principal) {
        return ResponseEntity.ok(conversationService.getConversationsSelling(clotheId, principal.getName()));
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<MessageDto>> getMessages(@RequestParam long buyerId, @RequestParam long clotheId, Principal principal) {
        return ResponseEntity.ok(conversationService.getMessages(buyerId, clotheId, principal.getName()));
    }

    //TODO: add endpoint to delete conversation's
}