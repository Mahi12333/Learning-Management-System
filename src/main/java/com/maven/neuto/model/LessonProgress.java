package com.maven.neuto.model;

import com.maven.neuto.emun.Complete;
import com.maven.neuto.emun.Status;
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
@Table(name = "tbl_lesson_progress")
public class LessonProgress extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "progress")
    private Float progress;

    @Column(name = "is_ongoing")
    private Boolean isOngoing;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Complete status = Complete.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

}
