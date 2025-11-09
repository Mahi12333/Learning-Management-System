package com.maven.neuto.model;


import com.maven.neuto.emun.EventRole;
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
@Table(name = "tbl_event_user")
public class EventUser extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User eventUser;

    @Column(name = "roomId")
    private Integer roomId;

    @Column(name = "event_link")
    private String eventLink;

    @Column(name = "event_token")
    private String eventToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventRole eventRole = EventRole.COST;
}
