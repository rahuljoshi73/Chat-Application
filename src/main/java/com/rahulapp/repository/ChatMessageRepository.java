package com.rahulapp.repository;

import com.rahulapp.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {

    @Query("SELECT c FROM ChatMessage c WHERE (c.senderId = :user1 AND c.recipientId = :user2) OR (c.senderId = :user2 AND c.recipientId = :user1) ORDER BY c.timestamp")
    List<ChatMessage> findChatHistory(@Param("user1") String user1, @Param("user2") String user2);

    List<ChatMessage> findChatHistoryByChatId(String chatId);
}

