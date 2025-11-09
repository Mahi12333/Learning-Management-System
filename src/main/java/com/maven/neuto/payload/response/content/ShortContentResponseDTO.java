package com.maven.neuto.payload.response.content;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ShortContentResponseDTO {
    private Long id;
    private String caption;
    private String thumbnail;
    private String imagesPath;
    private Long videoDuration;
    private Long size;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
