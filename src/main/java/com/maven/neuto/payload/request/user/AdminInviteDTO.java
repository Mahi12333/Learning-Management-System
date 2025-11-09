package com.maven.neuto.payload.request.user;

import com.maven.neuto.annotation.CustomeValidetion;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@CustomeValidetion
public class AdminInviteDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
}
