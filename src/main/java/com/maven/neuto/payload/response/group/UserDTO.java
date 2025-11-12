package com.maven.neuto.payload.response.group;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private Long id;
    private String name;
    private String username;
    private String avatar;
}
