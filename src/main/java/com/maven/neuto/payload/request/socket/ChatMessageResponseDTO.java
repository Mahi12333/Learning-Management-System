package com.maven.neuto.payload.request.socket;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;


@Setter @Getter @AllArgsConstructor @NoArgsConstructor
public class ChatMessageResponseDTO {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String message;
    private Instant createdAt;
    private Instant updatedAt;
}
