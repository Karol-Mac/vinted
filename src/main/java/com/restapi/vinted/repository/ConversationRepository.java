package com.restapi.vinted.repository;

import com.restapi.vinted.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByBuyerIdAndClotheId(Long buyerId, Long clotheId);

    Set<Conversation> findByBuyerId(Long buyerId);

    Set<Conversation> findByClotheId(Long clotheId);
}
