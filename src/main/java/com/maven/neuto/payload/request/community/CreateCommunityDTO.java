package com.maven.neuto.payload.request.community;


import com.maven.neuto.annotation.CustomeValidetion;
import lombok.*;


@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@CustomeValidetion
public class CreateCommunityDTO {
    private String name;
    private String tagLine;
    private String colorCode;
    private Long industryId;
    private String imagesPath;
    private Integer size;
}
