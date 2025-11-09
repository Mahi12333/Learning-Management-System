package com.maven.neuto.payload.request.socket;


import com.maven.neuto.annotation.CustomeValidetion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter @AllArgsConstructor @NoArgsConstructor @CustomeValidetion
public class ReportUserDTO {
    private Long reportedUserId;
    private String reason;
}
