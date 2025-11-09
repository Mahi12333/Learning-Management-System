package com.maven.neuto.payload.response.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommunityLoginResgistrationDTO {
    private String colorCode;
    private String logoUrl;
    private String communityName;
}
