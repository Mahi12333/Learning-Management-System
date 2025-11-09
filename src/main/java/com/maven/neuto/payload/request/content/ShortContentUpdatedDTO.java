package com.maven.neuto.payload.request.content;


import com.maven.neuto.annotation.CustomeValidetion;
import lombok.*;


@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@CustomeValidetion
public class ShortContentUpdatedDTO {
    private Long id;
    private String caption;
    private String thumbnail;
    private String imagesPath;
    private Long videoDuration;
    private Long size;
    private boolean onlyFetch;
}
