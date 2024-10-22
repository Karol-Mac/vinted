package com.restapi.vinted.utils;

import com.restapi.vinted.entity.Conversation;
import com.restapi.vinted.entity.Message;
import com.restapi.vinted.exception.ResourceNotFoundException;
import com.restapi.vinted.payload.ConversationDto;
import com.restapi.vinted.payload.MessageDto;
import com.restapi.vinted.repository.ConversationRepository;
import org.springframework.stereotype.Component;

@Component
public class MessagingUtils {

    private final ConversationRepository conversationRepository;
    private final UserUtils userUtils;

    public MessagingUtils(ConversationRepository conversationRepository, UserUtils userUtils){
        this.conversationRepository = conversationRepository;
        this.userUtils = userUtils;
    }

    public Conversation getConversation(long conversationId){
        return conversationRepository.findById(conversationId)
                .orElseThrow( () -> new ResourceNotFoundException("Conversation", "id", conversationId));
    }

    public boolean isBuyer(Conversation conversation, String email) {
        var currentUser = userUtils.getUser(email);

        return conversation.getBuyer().equals(currentUser);
    }

    public ConversationDto mapToDto(Conversation conversation){
        return ConversationDto.builder()
                .buyerId(conversation.getBuyer().getId())
                .clotheId(conversation.getClothe().getId())
                .id(conversation.getId())
                .build();
    }

    public MessageDto mapToDto(Message message){
        return MessageDto.builder()
                .isBuyer(message.isBuyer())
                .clotheId(message.getConversation().getClothe().getId())
                .buyerId(message.getConversation().getBuyer().getId())
                .messageContent(message.getMessage())
                .build();
    }
}