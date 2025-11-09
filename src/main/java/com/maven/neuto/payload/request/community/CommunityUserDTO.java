package com.maven.neuto.payload.request.community;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommunityUserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String profileImage;
    private boolean isOnline;
    private LocalDateTime lastSeen;
    private boolean isFollowing;
    private boolean isFollower;
}
