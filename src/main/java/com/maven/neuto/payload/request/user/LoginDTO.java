package com.maven.neuto.payload.request.user;

import com.maven.neuto.annotation.CustomeValidetion;
import com.maven.neuto.annotation.OptionalField;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@CustomeValidetion
public class LoginDTO {
    private String email;
    private String password;
    @OptionalField
    private String fcmToken;
}
