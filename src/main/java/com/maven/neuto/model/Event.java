package com.maven.neuto.model;

import com.maven.neuto.emun.EventStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_event",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name") // enforce unique name
        },
        indexes = {
                @Index(name = "idx_event_name", columnList = "name"),
                @Index(name = "idx_event_user", columnList = "user_id"),
                @Index(name = "idx_event_community", columnList = "community_id")
        }
)
public class Event extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User creator;

    @Column(name = "images_path")
    private String imagesPath;

    @Column(name = "name")
    private String name;

    @Lob
    @Column(name = "description")
    private String description;

    @Lob
    @Column(name = "about")
    private String about;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "recording_link")
    private String recordingLink;

    @Column(name = "roomId")
    private String roomId;

    @Column(name = "size")
    private Long size;

    @Column(name = "timezone", nullable = false)
    private String timezone; // e.g., "Asia/Kolkata", "Europe/London"

    @Enumerated(EnumType.STRING)
    private EventStatus status = EventStatus.UPCOMING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community eventCommunity;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventUser> events = new ArrayList<>();

}
