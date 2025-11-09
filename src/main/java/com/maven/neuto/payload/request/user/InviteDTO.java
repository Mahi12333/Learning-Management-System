package com.maven.neuto.payload.request.user;

import com.maven.neuto.annotation.CustomeValidetion;
import com.maven.neuto.annotation.OptionalField;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@CustomeValidetion
@Setter
@Getter
public class InviteDTO {
    private String email;
    private String userType;
    private String inviteType;

    @OptionalField
    private LocalDateTime expiredDate;
}
