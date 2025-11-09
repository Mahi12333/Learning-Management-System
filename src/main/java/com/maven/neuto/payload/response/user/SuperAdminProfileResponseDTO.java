package com.maven.neuto.payload.response.user;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SuperAdminProfileResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String phone;
    private String profileImage;
    private String roleId;
    private String profileComplete;
    private String communityComplete;
    private String fcmToken;
    private String status;
    private String createdAt;
    private String twitter;
    private String linkedId;
    private String instagram;
    private String facebook;
    private String coverPhoto;
    private String portfolioLink;
    private String step;
    private String loginFrom;
    private LocalDateTime dob;
    private LocalDateTime expiredDate;
    private Integer invitedUserCount;
}
