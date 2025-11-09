package com.maven.neuto.payload.request.user;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String mobile;
    private String city;
    private String profilePicture;
    private List<String> interest;
    private String status;
    private String roleName; // role name instead of entity
}
