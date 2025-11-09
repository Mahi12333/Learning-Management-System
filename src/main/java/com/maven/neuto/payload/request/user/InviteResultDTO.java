package com.maven.neuto.payload.request.user;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InviteResultDTO {
    private String email;
    private String details;
    private String reason;
    private String status;
}
