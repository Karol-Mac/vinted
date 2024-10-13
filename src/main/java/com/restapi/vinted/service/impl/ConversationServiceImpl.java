package com.restapi.vinted.service.impl;

import com.restapi.vinted.entity.Conversation;
import com.restapi.vinted.exception.ResourceNotFoundException;
import com.restapi.vinted.repository.ClotheRepository;
import com.restapi.vinted.repository.ConversationRepository;
import com.restapi.vinted.repository.UserRepository;
import com.restapi.vinted.service.ConversationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class ConversationServiceImpl implements ConversationService {

    private final ClotheRepository clotheRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    public ConversationServiceImpl(ClotheRepository clotheRepository, ConversationRepository conversationRepository, UserRepository userRepository){
        this.clotheRepository = clotheRepository;
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void startConversation(long clotheId, String email){
        var clothe = clotheRepository.findById(clotheId)
                .orElseThrow(() ->new ResourceNotFoundException("Clothe", "id", clotheId));

        var buyer = userRepository.findByEmail(email).get();

        var conversation = Conversation.builder()
                                        .buyer(buyer)
                                        .clothe(clothe)
                                        .build();

        conversationRepository.save(conversation);
    }
}
