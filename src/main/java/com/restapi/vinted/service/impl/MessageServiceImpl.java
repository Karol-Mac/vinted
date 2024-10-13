package com.restapi.vinted.service.impl;

import com.restapi.vinted.entity.Message;
import com.restapi.vinted.exception.ApiException;
import com.restapi.vinted.exception.ResourceNotFoundException;
import com.restapi.vinted.payload.MessageDto;
import com.restapi.vinted.repository.ClotheRepository;
import com.restapi.vinted.repository.ConversationRepository;
import com.restapi.vinted.repository.MessageRepository;
import com.restapi.vinted.repository.UserRepository;
import com.restapi.vinted.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessageServiceImpl implements MessageService {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final ClotheRepository clotheRepository;

    public MessageServiceImpl(UserRepository userRepository, MessageRepository messageRepository,
                      ConversationRepository conversationRepository, ClotheRepository clotheRepository){
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.conversationRepository = conversationRepository;
        this.clotheRepository = clotheRepository;
    }

    @Override
    @Transactional
    public void sendMessage(long buyiedId, long clotheId, String message, String emial) {

        var currentUser = userRepository.findByEmail(emial).get();
        var conversation = conversationRepository.findByBuyerIdAndClotheId(buyiedId, clotheId)
                .orElseThrow( () -> new ResourceNotFoundException("Conversation", "buyierId or clotheId"));

        boolean isBuyier;
        var clothe = clotheRepository.findById(clotheId).get();

        if(currentUser.getId() == conversation.getBuyer().getId()) {
            isBuyier = true;
        } else if (currentUser.getId() == clothe.getUser().getId()) {
            isBuyier = false;
        } else {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        var messageDto = new MessageDto(buyiedId, clotheId, message, isBuyier);

        saveMessage(messageDto);
    }

    private void saveMessage(MessageDto messageDto) {
        var conversation = conversationRepository
                .findByBuyerIdAndClotheId(messageDto.getBuyierId(), messageDto.getClotheId()).get();

        var message = new Message();
        message.setMessage(messageDto.getMessageContent());
        message.setBuyer(messageDto.isBuyer());
        message.setConversation(conversation);

        messageRepository.save(message);
    }
}