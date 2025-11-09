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
@Table(name = "tbl_group",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "name")
        },
        indexes = {
                @Index(name = "idx_group_name", columnList = "name"),
                @Index(name = "idx_group_user", columnList = "user_id"),
                @Index(name = "idx_group_community", columnList = "community_id")
        }
)
public class Group extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User createdBy;

    @Column(name = "name")
    private String name;

    @Column(name = "images_path")
    private String imagesPath;

    @Column(name = "tags", columnDefinition = "TEXT")
    private List<String> tags = new ArrayList<>();

    @Lob
    @Column(name = "description")
    private String description;

    @Lob
    @Column(name = "about")
    private String about;

    @Column(name = "size")
    private Long size;

    @Column(name = "privacy")
    private Boolean privacy = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;


}
