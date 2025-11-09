package com.maven.neuto.payload.response.user;


import com.maven.neuto.payload.request.user.InviteResultDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InvitedResponse {
    private int total;
    private int invited;
    private int skipped;
    private int failed;
    private List<InviteResultDTO> results;
}
