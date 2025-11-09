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
@Table(name = "tbl_banner")
public class Banner extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "user_id")
    private Long bannerCreator;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "size")
    private Long size;

    @Column(name = "community_id")
    private Long communityId;

    @Column(name = "description")
    private String description;

    @Column(name = "course_id")
    private String bannerCourse;
}
