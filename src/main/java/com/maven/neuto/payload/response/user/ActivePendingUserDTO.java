package com.maven.neuto.payload.response.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ActivePendingUserDTO {
    private List<UserDetailsDTO> member;
    private List<UserDetailsDTO> admin;
    private List<UserDetailsDTO> teacher;
    private Integer totalCount;
}
