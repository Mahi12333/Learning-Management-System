package com.maven.neuto.payload.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileCompleteDTO {
    private String firstName;
    private String lastName;
    private String city;
    private LocalDate dob;
    private String profilePicture;
    @NotBlank(message = "Step is mandatory")
    private String step;
    private List<String> interest;
}
