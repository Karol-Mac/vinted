package com.restapi.vinted.service;

import com.restapi.vinted.payload.ConversationDto;
import com.restapi.vinted.payload.MessageDto;

import java.util.List;

public interface ConversationService {
    void startConversation(long clotheId, String email);

    List<ConversationDto> getConversationsBuying(String email);

    List<MessageDto> getMessages(long buyerId, long clotheId, String email);

    List<ConversationDto> getConversationsSelling(long clotheId, String name);
}
