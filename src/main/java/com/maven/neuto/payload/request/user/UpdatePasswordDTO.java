package com.maven.neuto.payload.request.user;

import com.maven.neuto.annotation.CustomeValidetion;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@CustomeValidetion
public class UpdatePasswordDTO {
    private String oldPassword;
    private String newPassword;
}
