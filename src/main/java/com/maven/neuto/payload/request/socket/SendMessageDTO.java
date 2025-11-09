package com.maven.neuto.payload.request.socket;

import lombok.NoArgsConstructor;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageDTO {
    private Long receiverId;
    private String message;
}
