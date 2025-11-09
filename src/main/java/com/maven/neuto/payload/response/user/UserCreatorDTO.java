package com.maven.neuto.payload.response.user;

import lombok.*;

import java.util.List;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserCreatorDTO {
    private String firstName;
    private String lastName;
    private String userName;
    private String profilePicture;
    private String role;
}
