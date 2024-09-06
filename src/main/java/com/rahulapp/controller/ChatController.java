package com.rahulapp.controller;

import com.rahulapp.entity.*;
import com.rahulapp.repository.ChatMessageRepository;
import com.rahulapp.repository.ChatRoomRepository;
import com.rahulapp.repository.UserRepository;
import com.rahulapp.service.ChatRoomService;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Controller
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;

    private final ChatRoomService service;

    private final ChatMessageRepository chatMessageRepository;

    public ChatController(SimpMessagingTemplate messagingTemplate, UserRepository userRepository, ChatRoomRepository
            chatRoomRepository, ChatRoomService service, ChatMessageRepository chatMessageRepository) {
        this.messagingTemplate = messagingTemplate;
        this.userRepository = userRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.service = service;
        this.chatMessageRepository = chatMessageRepository;
    }

    @MessageMapping("chat/{chatId}")
    public String sendPrivateMessage(@DestinationVariable String chatId, ChatMessage message, Principal principal) {
        // Get the sender's username from the Principal object
        String senderId = userRepository.findByEmail(principal.getName()).getId().toString();

        // Perform any necessary processing or validation with the message
        LocalDateTime timestamp = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        timestamp.format(formatter);
        String recipientId = service.getRecipientIdFromChatId(senderId, chatId);

        // Store the message in the database
        ChatMessage chatMessage = ChatMessage.builder()
                .id(UUID.randomUUID().toString())
                .message(message.getMessage())
                .senderId(senderId)
                .recipientId(recipientId)
                .timestamp(timestamp).chatId(chatId).build();
           


        // Send the private message to the recipient's user-specific destination

        messagingTemplate.convertAndSendToUser(chatId, "queue/mess/" + chatId,
                HtmlUtils.htmlEscape( message.getMessage()));
        return "redirect:/chatRoom/" + chatId;
    }

    @GetMapping("/chatRoom")
    public String chatRoomPage(@RequestParam("recipientId") String recipientId, Model model, Principal principal) {
        String senderId = userRepository.findByEmail(principal.getName()).getId().toString();
        String chatId;

        // Sort the sender and recipient IDs to ensure consistent chat ID creation
        String[] ids = {senderId, recipientId};
        Arrays.sort(ids);
        chatId = ids[0] + "_" + ids[1];

        // Pass the chatId and recipientId to the view
        model.addAttribute("chatId", chatId);
        model.addAttribute("recipientId", recipientId);

        if (chatRoomRepository.findByChatId(chatId).isEmpty()) {
            // Create a new ChatRoom entry only if it doesn't exist
            ChatRoom newChatRoom = new ChatRoom();
            newChatRoom.setChatId(chatId);
            newChatRoom.setUser1(senderId);
            newChatRoom.setUser2(recipientId);
            chatRoomRepository.save(newChatRoom);
        }

        return "redirect:/chatRoom/" + chatId;
    }

    @GetMapping("/chatRoom/{chatId}")
    public String chatRoom(@PathVariable("chatId") String chatId,Model model,Principal principal) {

        if(!chatId.contains(userRepository.findByEmail(principal.getName()).getId().toString()))
            return "redirect:/user/main";

        List<ChatMessage> chatHistory = chatMessageRepository.findChatHistoryByChatId(chatId);
        String senderName = userRepository.findByEmail(principal.getName()).getName();
        model.addAttribute("senderName", senderName);
        // Pass the chat history to the view
        model.addAttribute("chatHistory", chatHistory);
        return "chatRoom";
    }

}
