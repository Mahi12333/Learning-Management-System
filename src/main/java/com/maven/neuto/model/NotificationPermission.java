package com.maven.neuto.model;


import jakarta.persistence.*;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "tbl_notificationPermission",
indexes = {
        @Index(name = "idx_user", columnList = "user_id")
})
public class NotificationPermission extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User notificationPermissionUser;

    @Column(name = "follow_notification")
    private Boolean followNotification = false;

    @Column(name = "reply_notification")
    private Boolean replyNotification = false;

    @Column(name = "course_notification")
    private Boolean courseNotification = false;

    @Column(name = "event_notification")
    private Boolean eventNotification = false;

    @Column(name = "group_notification")
    private Boolean groupNotification = false;
}
