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
@Table(name = "tbl_course",
         uniqueConstraints = {
              @UniqueConstraint(columnNames = "name"),
              @UniqueConstraint(columnNames = "slug")
         },
            indexes = {
                @Index(name = "idx_course_slug", columnList = "slug"),
                @Index(name = "idx_course_name", columnList = "name")
            }
    )
public class Course extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User courseCreator;

    @Column(name = "name")
    private String name;

    @Lob
    @Column(name = "about", columnDefinition = "TEXT")
    private String about;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;


    @Column(name = "images_path")
    private String imagesPath;

    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags = "[]";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community courseCommunity;

    @Column(name = "slug")
    private String slug;

    @Column(name = "archive")
    private Boolean archive = false; // true = archived, false = not archived

    @Column(name = "size")
    private Long size;

    // Course → Comments
    @OneToMany(mappedBy = "commentCourse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Module> modules = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lesson> lessons = new ArrayList<>();

//    @OneToMany(mappedBy = "bannerCourse", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Banner> bannerCourse = new ArrayList<>();

}
