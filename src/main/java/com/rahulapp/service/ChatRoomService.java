package com.rahulapp.service;

import com.rahulapp.entity.ChatRoom;
import com.rahulapp.repository.ChatRoomRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChatRoomService {

    final private ChatRoomRepository chatRoomRepository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }

    public String getRecipientIdFromChatId(String chatId, String senderId) {
        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findByChatId(chatId);
        if (optionalChatRoom.isPresent()) {
            ChatRoom chatRoom = optionalChatRoom.get();
            // Determine the recipient's ID based on user1 and user2 fields
            String user1 = chatRoom.getUser1();
            String user2 = chatRoom.getUser2();

            // Check if senderId matches user1 or user2
            if (senderId.equals(user1)) {
                return user2; // user2 is the recipient
            } else if (senderId.equals(user2)) {
                return user1; // user1 is the recipient
            } else {
                // Handle the case when the senderId does not match user1 or user2
                return null;
            }
        } else {
            // Handle the case when the chat room does not exist
            return null;
        }
    }

}
