package com.maven.neuto.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_group_comment",
        indexes = {
                @Index(name = "idx_group_parent", columnList = "group_id, parent_id"),
                @Index(name = "idx_group_comments_user_id", columnList = "user_id"),
                @Index(name = "idx_group_comment_id", columnList = "id")
        })
public class GroupComment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // GroupComment ↔ User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // GroupComment ↔ Group
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(name = "content")
    private String content;

    // GroupComment ↔ Parent (self reference)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private GroupComment parent;

    // Reply to a specific User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_to_user_id")
    private User replyToUser;

    // Reply to another GroupComment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_to_comment_id")
    private GroupComment replyToComment;

    // Likes on this group comment
    @OneToMany(mappedBy = "groupComment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupCommentLike> likes = new ArrayList<>();

}
