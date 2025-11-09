package com.maven.neuto.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;


@Builder
@Entity
@Table(name = "tbl_report_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportUser extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Reporter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    // Reported user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_id", nullable = false)
    private User reported;

    @Lob
    @Column(name = "reason", nullable = true)
    private String reason;

}
