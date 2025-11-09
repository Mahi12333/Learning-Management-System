package com.maven.neuto.payload.request.user;

import com.maven.neuto.annotation.CustomeValidetion;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@CustomeValidetion
public class ForgetPassword {
    private String email;
    private String newPassword;
}
