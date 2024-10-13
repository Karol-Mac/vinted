package com.restapi.vinted.controller;

import com.restapi.vinted.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/messages")
public class Messagecontroller {

    private final MessageService messageService;

    public Messagecontroller(MessageService messageService){
        this.messageService = messageService;
    }

    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(@RequestParam long buyerId,
                                      @RequestParam long clotheId,
                                      @RequestBody String message,
                                      Principal principal) {

        messageService.sendMessage(buyerId, clotheId, message, principal.getName());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
