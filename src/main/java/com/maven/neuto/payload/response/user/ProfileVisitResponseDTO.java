package com.maven.neuto.payload.response.user;

import com.maven.neuto.payload.response.content.UserShortContentResponseDTO;
import com.maven.neuto.payload.response.course.UserCourseResponseDTO;
import com.maven.neuto.payload.response.group.UserGroupResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileVisitResponseDTO {
    private ProfileVisitDTO user;
    private List<UserCourseResponseDTO> course;
    private List<UserShortContentResponseDTO> shortContent;
    private List<UserGroupResponseDTO> group;

}
