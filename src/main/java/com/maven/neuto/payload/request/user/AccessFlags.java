package com.maven.neuto.payload.request.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccessFlags {
    private boolean saveContentAccess = false;
    private boolean myContentAccess = false;
    private boolean logoutAccess = false;
    private boolean editAccess = false;
    private boolean courseAccess = true;
    private boolean groupAccess = true;
    private boolean shareProfileAccess = true;
    private boolean changeRoleAccess = false;
    private boolean removeCommunityAccess = false;
    private boolean blockUserAccess = false;
    private boolean reportUserAccess = false;
    private boolean archiveAccess = false;
}
