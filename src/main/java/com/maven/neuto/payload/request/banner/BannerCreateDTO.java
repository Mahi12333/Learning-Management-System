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
    public static final String TEXT_REGEX = "^[A-Za-z\\s]+$";

    @NotBlank(message = "Name is required.")
    @Pattern(regexp = TEXT_REGEX, message = "Name must contain only alphabets and spaces.")
    @Size(max = 100, message = "Name must not exceed 100 characters.")
    private String name;

    @NotBlank(message = "Description is required.")
    @Pattern(regexp = TEXT_REGEX, message = "Description must contain only alphabets and spaces.")
    @Size(max = 500, message = "Description must not exceed 500 characters.")
    private String description;

    @NotBlank(message = "Course slug is required.")
    private String courseSlug;

    @NotBlank(message = "Image URL is required.")
    private String imageUrl;

    private Integer size;
}
