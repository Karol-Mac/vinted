package com.restapi.vinted.service.impl;

import com.restapi.vinted.entity.Conversation;
import com.restapi.vinted.payload.ConversationDto;
import com.restapi.vinted.payload.MessageDto;
import com.restapi.vinted.repository.ConversationRepository;
import com.restapi.vinted.service.ConversationService;
import com.restapi.vinted.utils.ClotheUtils;
import com.restapi.vinted.utils.MessagingUtils;
import com.restapi.vinted.utils.UserUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final ClotheUtils clotheUtils;
    private final MessagingUtils messagingUtils;
    private final UserUtils userUtils;

    public ConversationServiceImpl(ConversationRepository conversationRepository,
                                   ClotheUtils clotheUtils, MessagingUtils messagingUtils, UserUtils userUtils){
        this.conversationRepository = conversationRepository;
        this.clotheUtils = clotheUtils;
        this.messagingUtils = messagingUtils;
        this.userUtils = userUtils;
    }

    @Override
    @Transactional
    public void startConversation(long clotheId, String email){
        var clothe = clotheUtils.getClotheFromDB(clotheId);

        var buyer = userUtils.getUser(email);

        if(clothe.getUser().getId() == buyer.getId())
            throw new AccessDeniedException("We dont't talk to ourselves");

        var conversation = Conversation.builder()
                .buyer(buyer)
                .clothe(clothe)
                .build();

        conversationRepository.save(conversation);
    }

    @Override
    public List<ConversationDto> getConversationsBuying(String email){

        var buyer = userUtils.getUser(email);
        return conversationRepository.findByBuyerId(buyer.getId())
                .stream()
                .map(messagingUtils::mapToDto)
                .toList();
    }

    @Override
    @PreAuthorize("@clotheUtils.isOwner(#clotheId, #email)")
    public List<ConversationDto> getConversationsSelling(long clotheId, String email){
        return conversationRepository.findByClotheId(clotheId)
                .stream()
                .map(messagingUtils::mapToDto)
                .toList();
    }

    @Override
    public List<MessageDto> getMessages(long buyerId, long clotheId, String email){
        if (!clotheUtils.isOwner(clotheId, email) && !messagingUtils.isBuyer(buyerId, clotheId, email))
            throw new AccessDeniedException("You don't have permission to see this message");

        var conversation = messagingUtils.getConversation(buyerId, clotheId);

        return conversation.getMessages().stream().map(messagingUtils::mapToDto).toList();
    }
}
