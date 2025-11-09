package com.maven.neuto.payload.request.course;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class TaskModuleCreateDTO {
    private String activityDetails;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean mandatory;
    private Long moduleId;
    private String permissionType;
    private Boolean permission;
    private List<Integer> paticipantList;
}
