package com.maven.neuto.model;


import com.maven.neuto.emun.UploadSectionName;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@Entity
@Table(name = "uploaded_files")
@AllArgsConstructor
@NoArgsConstructor
public class UploadedFile extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url")
    private String url;

    @Column(name = "public_id")
    private String publicId;

    @Column(name = "size")
    private Long size;

    @Column(name = "read")
    private Boolean read = false;

    @Column(name = "duration")
    private Long duration;

    @Enumerated(EnumType.STRING)
    @Column(name = "section_name")
    private UploadSectionName sectionName; // PROFILE, COURSE, LESSON

    @Column(name = "user_id")
    private Long uploadedBy;
}
