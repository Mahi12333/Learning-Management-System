package com.maven.neuto.payload.response.user;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserGroupparticipantsDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String userName;
    private String profilePicture;
    private String email;
}
