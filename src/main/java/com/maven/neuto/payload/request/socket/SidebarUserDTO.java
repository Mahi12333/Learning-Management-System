package com.maven.neuto.payload.request.socket;

import lombok.*;

import java.time.Instant;

@Setter @Getter @NoArgsConstructor @AllArgsConstructor @Builder
public class SidebarUserDTO {
    private Long id;
    private String userName;
    private String fullName;
    private String profileImageUrl;
    private boolean online;
    private Instant lastActive;
    private String message;
    private Long unReadMessageCount;
    private Instant lastMessageTime;
}
