package com.maven.neuto.model;

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
@Table(name = "tbl_group_comment_like", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "group_comment_id"})
})
public class GroupCommentLike extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The group comment this like belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_comment_id", nullable = false)
    private GroupComment groupComment;

    // Who liked the group comment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
