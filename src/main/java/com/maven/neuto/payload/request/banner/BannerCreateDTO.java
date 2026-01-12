package com.maven.neuto.payload.request.banner;


import com.maven.neuto.annotation.CustomeValidetion;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
@CustomeValidetion
public class BannerCreateDTO {
//    public static final String TEXT_REGEX = "^[A-Za-z\\s]+$";
    private String name;
    private String description;
    private Long courseId;
    private String imageUrl;
    private Long size;
}
