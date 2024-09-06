package com.rahulapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ChatMessage implements Serializable {
    @Id
    private String id;

    private String senderId;
    private String recipientId;
    private String message;
    private LocalDateTime timestamp;
    private String chatId;

}

