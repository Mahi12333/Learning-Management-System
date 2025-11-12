package com.maven.neuto.payload.request.group;

import com.maven.neuto.annotation.CustomeValidetion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Setter
@Getter
@CustomeValidetion
public class GroupCreateDTO {
    public static final String TEXT_REGEX = "^[A-Za-z\\s]+$";

    @Pattern(regexp = TEXT_REGEX, message = "Name must contain only alphabets and spaces.")

    private String name;
    @Pattern(regexp = TEXT_REGEX, message = "Name must contain only alphabets and spaces.")
    private String description;

    private String rule;
    private String about;
    private List<String> tags;
    private String imagesPath;
    private Long size;
}
