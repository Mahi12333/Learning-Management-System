package com.maven.neuto.payload.response.community;


import com.maven.neuto.payload.response.PaginatedResponse;
import com.maven.neuto.payload.response.user.UserResponseDTO;
import lombok.*;


@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SuperAdminCommunityPageResponseDTO {
    private Long totalTeachers;
    private Long totalMembers;
    private Long totalCourses;
    private Long totalLessons;
    private Long totalModules;
    private Long totalGroups;
    private Long totalShortContents;
    private Long totalBanners;
    private Long totalStorageSize; // Sum of size fields
    private CommunityResponseDTO communityResponseDTO;
    private PaginatedResponse<UserResponseDTO> users;
}
