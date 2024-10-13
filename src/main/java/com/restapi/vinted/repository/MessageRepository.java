package com.restapi.vinted.repository;

import com.restapi.vinted.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

}
