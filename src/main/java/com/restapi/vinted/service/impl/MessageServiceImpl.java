package com.restapi.vinted.service.impl;

import com.restapi.vinted.exception.ApiException;
import com.restapi.vinted.payload.MessageDto;
import com.restapi.vinted.repository.MessageRepository;
import com.restapi.vinted.service.MessageService;
import com.restapi.vinted.utils.ClotheUtils;
import com.restapi.vinted.utils.MessagingUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ClotheUtils clotheUtils;
    private final MessagingUtils messageUtils;

    public MessageServiceImpl(MessageRepository messageRepository, ClotheUtils clotheUtils, MessagingUtils messageUtils){
        this.messageRepository = messageRepository;
        this.clotheUtils = clotheUtils;
        this.messageUtils = messageUtils;
    }

    //FIXME: tak na prawdę wysyłanie wiadomości powinno być w serwisie ConversationService
    // bo to jest związane z konkretną konwersacją!

    //TODO: zmień logikę, żeby wymagało tylko ID konwersacji, przenieś to do ConversationService
    //  + pamiętaj o kontrolerach
    @Override
    @Transactional
    public void sendMessage(long buyedId, long clotheId, String message, String email) {

        boolean isBuyer;
        if(messageUtils.isBuyer(buyedId, clotheId, email)) isBuyer = true;
        else if (clotheUtils.isOwner(clotheId, email)) isBuyer = false;
        else throw new ApiException(HttpStatus.UNAUTHORIZED, "Unauthorized");

        var messageDto = new MessageDto(buyedId, clotheId, message, isBuyer);
        saveMessage(messageDto);
    }

    @Override
    public void saveMessage(MessageDto messageDto) {
        messageRepository.save(messageUtils.mapToEntity(messageDto));
    }
}