package com.maven.neuto.payload.response.community;

import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommunityResponseDTO {
    private Long id;
    private String name;
    private Long communityCreatedUser;
    private String tagline;
    private String imagesPath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
