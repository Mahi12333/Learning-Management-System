package com.maven.neuto.model;


import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
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
@Table(name = "tbl_community")
public class Community extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User communityCreatedUser;

    @Column(name = "name")
    private String name;

    @Column(name = "tagline", columnDefinition = "TEXT")
    private String tagline;

    @Column(name = "images_path")
    private String imagesPath;

    @Column(name = "color_code")
    private String colorCode = "#FF4E6B"; // default value

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "industry_id")
    private Industry industry;

    @Column(name = "size")
    private Integer size;

    @OneToMany(mappedBy = "userCommunity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Group> groups = new ArrayList<>();

    @OneToMany(mappedBy = "eventCommunity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> events = new ArrayList<>();

//    @OneToMany(mappedBy = "bannerCommunity", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Banner> banner = new ArrayList<>();

    @OneToMany(mappedBy = "courseCommunity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Course> courses = new ArrayList<>();

    @OneToMany(mappedBy = "shortContentCommunity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShortContentUpload> shortContentUploads = new ArrayList<>();

    @OneToMany(mappedBy = "lessonCommunity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lesson> lessons = new ArrayList<>();

}
