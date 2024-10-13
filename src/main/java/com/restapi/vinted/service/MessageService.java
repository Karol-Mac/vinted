package com.restapi.vinted.service;

import com.restapi.vinted.payload.MessageDto;

public interface MessageService {
    void sendMessage(long buyiedId, long clotheId, String message, String email);

}
