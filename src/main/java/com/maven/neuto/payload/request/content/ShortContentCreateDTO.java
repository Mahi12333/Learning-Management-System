package com.maven.neuto.payload.request.content;

import com.maven.neuto.annotation.CustomeValidetion;
import lombok.*;


@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@CustomeValidetion
public class ShortContentCreateDTO {
    private String caption;
    private String thumbnail;
    private String imagesPath;
    private Long videoDuration;
    private Long size;
}
