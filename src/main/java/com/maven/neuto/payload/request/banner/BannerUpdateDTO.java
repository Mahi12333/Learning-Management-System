package com.maven.neuto.payload.request.banner;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BannerUpdateDTO {
    private String name;
    private String description;
    private String courseSlug;
    private Boolean onlyFetch;
    private Long bannerId;
    private String imageUrl;
    private Integer size;
}
