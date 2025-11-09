package com.maven.neuto.payload.request.community;

import lombok.*;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatedCommunityDTO {
    private Long id;
    private String name;
    private String tagLine;
    private String colorCode;
    private Long industryId;
    private String imagesPath;
    private Integer size;
}
