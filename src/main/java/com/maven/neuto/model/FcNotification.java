package com.maven.neuto.model;


import com.maven.neuto.emun.NotificationStatus;
import com.maven.neuto.emun.SourceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_notification",
indexes = {
        @Index(name = "idx_receiver", columnList = "receiver_id")
})
public class FcNotification extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // sender
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User senderNotification;

    // receiver user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiverNotification;

    @Column(name = "source_id")
    private Integer sourceId;

    @Column(name = "title")
    private String title;

    @Column(name = "message")
    private String message;

    @Column(name = "icon")
    private String icon;

    @Column(name = "redirect_url")
    private String redirectUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SourceType sourceType = SourceType.NONE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus notificationStatus = NotificationStatus.NONE;

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "slug")
    private String slug;

}
