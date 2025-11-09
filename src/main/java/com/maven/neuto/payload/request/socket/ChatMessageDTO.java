package com.maven.neuto.payload.request.socket;


import lombok.*;

import java.time.Instant;

@Setter @Getter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChatMessageDTO {
    private Long receiverId;
    private Long senderId;
    private Long conversationId;
    private String message;
    private boolean isSeen;
    private Instant createdAt;
    private Instant updatedAt;
}
