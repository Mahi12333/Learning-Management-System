package com.maven.neuto.payload.request.user;

import com.maven.neuto.annotation.CustomeValidetion;
import com.maven.neuto.annotation.OptionalField;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@CustomeValidetion
public class SignupDTO {
    private String email;
    private String password;
    @OptionalField
    private String fcmToken;
}
