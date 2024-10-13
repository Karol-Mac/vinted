package com.restapi.vinted.repository;

import com.restapi.vinted.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByBuyerIdAndClotheId(Long buyerId, Long clotheId);


}
