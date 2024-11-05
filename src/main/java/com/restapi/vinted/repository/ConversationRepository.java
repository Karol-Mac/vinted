package com.restapi.vinted.repository;

import com.restapi.vinted.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.stream.Stream;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Stream<Conversation> findByClotheId(Long clotheId);

    Stream<Conversation> findByBuyerEmail(String email);
}
