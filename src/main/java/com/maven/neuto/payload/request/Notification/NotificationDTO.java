package com.maven.neuto.payload.request.Notification;

import lombok.*;


@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private String Title;
    private String Body;
}
