package com.maven.neuto.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
@Table(name = "tbl_short_content_upload",
        indexes = {
        @Index(name = "idx_short_content_upload_community", columnList = "community_id")
        }
)
public class ShortContentUpload extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User creator;

    @Column(name = "caption")
    private String caption;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "images_path")
    private String imagesPath;

    @Column(name = "video_duration")
    private Long videoDuration;

    @Column(name = "size")
    private Long size;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community shortContentCommunity;

    @OneToMany(mappedBy = "bookmarkShortContent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bookmark> bookmarkShortContent = new ArrayList<>();

}
