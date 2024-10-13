package com.restapi.vinted.controller;

import com.restapi.vinted.service.ConversationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class ConversationController {
    private final ConversationService conversationService;


    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @PostMapping("/conversation")
    public ResponseEntity<Void> startConversation(@RequestParam long clotheId, Principal principal) {

        conversationService.startConversation(clotheId, principal.getName());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
