package com.maven.neuto.payload.response.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {
    private String accessToken;
    private String refreshToken;
    private UserDetailsDTO user;
}
