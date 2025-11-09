package com.maven.neuto.model;

import com.maven.neuto.emun.Complete;
import com.maven.neuto.emun.PermissionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_task_module")
public class TaskModule extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Lob
    @NotBlank(message = "Activity Details cannot be blank. Please provide a value.")
    @Column(name = "activity_details")
    private String activityDetails;

    @Column(name = "color_code")
    private String colorCode = "#FF4E6B"; // default value

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PermissionType status = PermissionType.NONE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private Module module;

    @NotBlank(message = "Start Date cannot be blank. Please provide a value.")
    @Column(name = "start_date")
    private LocalDate startDate;

    @NotBlank(message = "End Date cannot be blank. Please provide a value.")
    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "permission")
    private Boolean permission = false;

    @Column(name = "mendatory")
    private Boolean mendatory = false;

    @Column(name = "paticipant_list")
    private List<Integer> paticipantList;

    @OneToMany(mappedBy = "taskModule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ModuleTaskSubmit> submissions = new ArrayList<>();
}
