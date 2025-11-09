package com.maven.neuto.payload.request.user;

import com.maven.neuto.annotation.CustomeValidetion;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@CustomeValidetion
public class ForgetPasswordTokenVerifyDTO {
    private String token;
    private String password;
}
