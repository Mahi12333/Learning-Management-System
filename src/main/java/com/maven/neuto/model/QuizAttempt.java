package com.maven.neuto.model;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.util.Map;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_quiz_attempt")
public class QuizAttempt extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User quizUser;

    @Column(name = "score", nullable = true)
    private Integer score;

    @Column(name = "attempt_no", nullable = true)
    private Integer attemptNo;

    @Type(JsonType.class)
    @Column(columnDefinition = "json", nullable = true)
    private Map<String, Object> answer;

    @Type(JsonType.class)
    @Column(columnDefinition = "json", nullable = true)
    private Map<String, Object> userAnswer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private Module module;
}
