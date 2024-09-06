package com.rahulapp.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ChatRoom {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String chatId;
    private String user1;
    private String user2;
}
