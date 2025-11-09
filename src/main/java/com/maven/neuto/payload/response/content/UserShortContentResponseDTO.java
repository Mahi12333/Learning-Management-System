package com.maven.neuto.payload.response.content;


import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserShortContentResponseDTO {
    private Long id;
    private String imagesPath;
    private String thumbnail;
    private Long videoDuration;
    private Long size;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String caption;
    private Boolean isBookmark;
    private Boolean isLike;
}
