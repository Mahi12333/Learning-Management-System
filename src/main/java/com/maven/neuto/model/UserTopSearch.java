package com.maven.neuto.model;

import com.maven.neuto.emun.Type;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_user_top_search")
public class UserTopSearch extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User userTorSearch;

    @Enumerated(EnumType.STRING)
    private Type type = Type.NONE;

    @Column(name = "last_updated_index")
    private Integer lastUpdatedIndex = 0;

    @NotBlank(message = "Search cannot be blank. Please provide a value.")
    @Column(name = "search")
    private String search;

}
