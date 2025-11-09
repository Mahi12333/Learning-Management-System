package com.maven.neuto.payload.response.group;

import com.maven.neuto.payload.response.user.UserCreatorDTO;
import com.maven.neuto.payload.response.user.UserGroupparticipantsDTO;
import lombok.*;

import java.time.Instant;
import java.util.List;


@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserGroupResponseDTO {
    private Long id;
    private String name;
    private String imagesPath;
    private String description;
    private String about;
    private Long size;
    private Boolean privacy;
    private Instant createdDate;
    private Instant modifiedDate;

    private UserCreatorDTO createdBy;
    private List<UserGroupparticipantsDTO> users;
    private Boolean viewAccess;
}
