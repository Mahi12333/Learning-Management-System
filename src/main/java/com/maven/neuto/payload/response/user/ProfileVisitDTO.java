package com.maven.neuto.payload.response.user;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.maven.neuto.payload.request.user.AccessFlags;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileVisitDTO {
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
    private Integer FollowersCount;
    private Integer FollowingCount;
    private Boolean isFollowing;
    private Boolean isFollower;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private AccessFlags access;
}
