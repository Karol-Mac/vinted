package com.restapi.vinted.repository;

import com.restapi.vinted.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Set<Conversation> findByBuyerId(Long buyerId);

    Set<Conversation> findByClotheId(Long clotheId);
}
