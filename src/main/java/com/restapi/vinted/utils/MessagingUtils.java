package com.restapi.vinted.utils;

import com.restapi.vinted.entity.Conversation;
import com.restapi.vinted.entity.Message;
import com.restapi.vinted.entity.User;
import com.restapi.vinted.exception.ResourceNotFoundException;
import com.restapi.vinted.payload.ConversationDto;
import com.restapi.vinted.payload.MessageDto;
import com.restapi.vinted.repository.ConversationRepository;
import com.restapi.vinted.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class MessagingUtils {

    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    public MessagingUtils(ConversationRepository conversationRepository, UserRepository userRepository){
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
    }

    public Conversation getConversation(long buyedId, long clotheId){
        return conversationRepository.findByBuyerIdAndClotheId(buyedId, clotheId)
                .orElseThrow( () -> new ResourceNotFoundException("Conversation", "buyerId or clotheId"));
    }

    public boolean isBuyer(long buyerId, long clotheId, String email) {
        var conversation = getConversation(buyerId, clotheId);
        var currentUser = getUser(email);

        return conversation.getBuyer().equals(currentUser);
    }

    public User getUser(String email){
        return userRepository.findByEmail(email).orElseThrow(
                () -> new ResourceNotFoundException("User", "email", email));
    }

    public Message mapToEntity(MessageDto messageDto) {
        return Message.builder()
                .message(messageDto.getMessageContent())
                .isBuyer(messageDto.getIsBuyer())
                .conversation(conversationRepository
                        .findByBuyerIdAndClotheId(
                                messageDto.getBuyerId(),
                                messageDto.getClotheId()).get()).build();
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