package com.maven.neuto.model;


import com.maven.neuto.emun.BookMark;
import jakarta.persistence.*;
import lombok.*;


@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_bookmark")
public class Bookmark extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User bookmarkUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "short_content_id")
    private ShortContentUpload bookmarkShortContent;

   @Enumerated(EnumType.STRING)
    private BookMark type = BookMark.NONE;
}
