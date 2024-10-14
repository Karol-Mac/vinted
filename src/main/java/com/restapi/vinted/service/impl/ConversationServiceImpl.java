package com.restapi.vinted.service.impl;

import com.restapi.vinted.entity.Conversation;
import com.restapi.vinted.entity.Message;
import com.restapi.vinted.entity.User;
import com.restapi.vinted.exception.ResourceNotFoundException;
import com.restapi.vinted.payload.ConversationDto;
import com.restapi.vinted.payload.MessageDto;
import com.restapi.vinted.repository.ClotheRepository;
import com.restapi.vinted.repository.ConversationRepository;
import com.restapi.vinted.repository.UserRepository;
import com.restapi.vinted.service.ConversationService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class ConversationServiceImpl implements ConversationService {

    private final ClotheRepository clotheRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    public ConversationServiceImpl(ClotheRepository clotheRepository, ConversationRepository conversationRepository,
                                   UserRepository userRepository){
        this.clotheRepository = clotheRepository;
        this.conversationRepository = conversationRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void startConversation(long clotheId, String email){
        var clothe = clotheRepository.findById(clotheId)
                .orElseThrow(() -> new ResourceNotFoundException("Clothe", "id", clotheId));

        var buyer = getUser(email);
        var conversation = Conversation.builder()
                .buyer(buyer)
                .clothe(clothe)
                .build();

        conversationRepository.save(conversation);
    }

    @Override
    public List<ConversationDto> getConversationsBuying(String email){

        var user = getUser(email);
        return conversationRepository.findByBuyerId(user.getId())
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    @PreAuthorize("@clotheUtils.isOwner(#clotheId, #email)")
    public List<ConversationDto> getConversationsSelling(long clotheId, String email){
        return conversationRepository.findByClotheId(clotheId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<MessageDto> getMessages(long buyerId, long clotheId, String email){
        var user = getUser(email);
        var buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new ResourceNotFoundException("Is it from here ?", "id", buyerId));
        var clothe = clotheRepository.findById(clotheId)
                .orElseThrow(() -> new ResourceNotFoundException("Clothe", "id", clotheId));
        var owner = userRepository.findById(clothe.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner", "id", clothe.getUser().getId()));

        if (!owner.equals(user) && !owner.equals(buyer))
            throw new AccessDeniedException("You don't have permission to see this message");

        var conversation = conversationRepository.findByBuyerIdAndClotheId(buyerId, clotheId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation", "buyierId or clotheId"));

        return conversation.getMessages().stream().map(this::mapToDto).toList();
    }

    private User getUser(String email){
        return userRepository.findByEmail(email).get();
    }

    private ConversationDto mapToDto(Conversation conversation){
        return ConversationDto.builder()
                .buyerId(conversation.getBuyer().getId())
                .clotheId(conversation.getClothe().getId())
                .id(conversation.getId())
                .build();
    }

    private MessageDto mapToDto(Message message){
        return MessageDto.builder()
                .isBuyer(message.isBuyer())
                .clotheId(message.getConversation().getClothe().getId())
                .buyerId(message.getConversation().getBuyer().getId())
                .messageContent(message.getMessage())
                .build();
    }
}
