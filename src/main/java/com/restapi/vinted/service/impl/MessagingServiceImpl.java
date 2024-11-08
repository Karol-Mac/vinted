package com.restapi.vinted.service.impl;

import com.restapi.vinted.entity.Conversation;
import com.restapi.vinted.entity.Message;
import com.restapi.vinted.exception.ApiException;
import com.restapi.vinted.payload.ConversationDto;
import com.restapi.vinted.payload.MessageDto;
import com.restapi.vinted.repository.ConversationRepository;
import com.restapi.vinted.repository.MessageRepository;
import com.restapi.vinted.service.MessagingService;
import com.restapi.vinted.utils.ClotheUtils;
import com.restapi.vinted.utils.MessagingUtils;
import com.restapi.vinted.utils.UserUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class MessagingServiceImpl implements MessagingService {

    private final ConversationRepository conversationRepository;
    private final ClotheUtils clotheUtils;
    private final MessagingUtils messagingUtils;
    private final UserUtils userUtils;
    private final MessageRepository messageRepository;

    public MessagingServiceImpl(ConversationRepository conversationRepository,
                                ClotheUtils clotheUtils, MessagingUtils messagingUtils, UserUtils userUtils, MessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.clotheUtils = clotheUtils;
        this.messagingUtils = messagingUtils;
        this.userUtils = userUtils;
        this.messageRepository = messageRepository;
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
    @Transactional(readOnly = true)
    public List<ConversationDto> getConversationsBuying(String email) {
        return conversationRepository.findByBuyerEmail(email)
//                .stream()
                .map(messagingUtils::mapToDto)
                .toList();
    }

    @Override
    @PreAuthorize("@clotheUtils.isOwner(#clotheId, #email)")
    @Transactional(readOnly = true)
    public List<ConversationDto> getConversationsSelling(long clotheId, String email) {
        return conversationRepository.findByClotheId(clotheId)
                .map(messagingUtils::mapToDto)
                .toList();
    }

    @Override
    public List<MessageDto> getMessages(long conversationId, String email) {
        var conversation = messagingUtils.getConversation(conversationId);
        var currentUser = userUtils.getUser(email);

        boolean isAuthorized = conversation.getBuyer().getId() == currentUser.getId() ||
                clotheUtils.isOwner(conversation.getClothe().getId(), email);

        if (!isAuthorized)
            throw new AccessDeniedException("You don't have permission to see this message");

        return conversation.getMessages().stream().map(messagingUtils::mapToDto).toList();
    }

    @Override
    @Transactional
    public void sendMessage(long conversationId, String message, String email) {

        var conversation = messagingUtils.getConversation(conversationId);

        boolean isBuyer;
        if (messagingUtils.isBuyer(conversation, email))
            isBuyer = true;
        else if (clotheUtils.isOwner(conversation.getClothe().getId(), email))
            isBuyer = false;
        else throw new ApiException(HttpStatus.UNAUTHORIZED, "Unauthorized");

        var messageEntity = Message.builder()
                .conversation(conversation)
                .message(message)
                .isBuyer(isBuyer)
                .build();

        messageRepository.save(messageEntity);
    }
}
