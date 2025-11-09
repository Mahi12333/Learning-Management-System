package com.maven.neuto.model;

import com.maven.neuto.config.StringListConverter;
import com.maven.neuto.emun.LoginFrom;
import com.maven.neuto.emun.ProfileComplete;
import com.maven.neuto.emun.Status;
import com.maven.neuto.emun.Step;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = true)
    private String firstName;

    @Column(name = "last_name", nullable = true)
    private String lastName;

    @Column(name = "user_name", nullable = true)
    private String userName;

//    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = true)
    private String mobile;

    private String password;

    @Enumerated(EnumType.STRING)

    private LoginFrom loginFrom = LoginFrom.local;

    @Enumerated(EnumType.STRING)
    private Step step = Step.ONE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invitedby_id", nullable = true)
    private User invitedBy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = true)
    private Community userCommunity;

    @Column(name = "city", nullable = true)
    private String city;

    @Column(name = "dob", nullable = true)
    private LocalDate dob;

    @Column(name = "profile_picture", nullable = true)
    private String profilePicture;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> interest;

    private Boolean isOtpSend = false;

    @Enumerated(EnumType.STRING)
    private ProfileComplete profileComplete = ProfileComplete.PENDING;

    @Enumerated(EnumType.STRING)
    private ProfileComplete communityComplete = ProfileComplete.PENDING;

    @Enumerated(EnumType.STRING)
    private Status status = Status.INACTIVE;

    @Column(name = "fcm_token", nullable = true)
    private String fcmToken;

    private LocalDateTime lastLogin;
    private Instant lastSeen = Instant.now();

    @Column(name = "twitter", nullable = true)
    private String twitter;
    @Column(name = "linkedin", nullable = true)
    private String linkedin;
    @Column(name = "cover_photo", nullable = true)
    private String coverPhoto;
    @Column(name = "facebook", nullable = true)
    private String facebook;
    @Column(name = "instagram", nullable = true)
    private String instagram;
    @Column(name = "portfolio_link", nullable = true)
    private String portfolioLink;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String description;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String description2;

    @Column(name = "country", nullable = true)
    private String country;

    @Column(name = "expire_date")
    private LocalDate expireDate;

    private Boolean reportUser = false;
    private Boolean blockUser = false;
    private Boolean removeCommunity = false;
    private Boolean isOnline = false;

    @Column(name = "size", nullable = true)
    private Integer size;

    private Integer lastInterestIndex = -1;
    private String setSignUpMethod;
    private boolean isTwoFactorEnabled = false;

    // Sent Messages
    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private List<ChatMessage> sentMessages = new ArrayList<>();

    // Received Messages
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL)
    private List<ChatMessage> receivedMessages = new ArrayList<>();

    //! This is Many-to-Many relationship or what??
    @OneToMany(mappedBy = "chatBlocker", cascade = CascadeType.ALL)
    private List<ChatBlockedUser> blockerUsers = new ArrayList<>();

    @OneToMany(mappedBy = "chatBlocked", cascade = CascadeType.ALL)
    private List<ChatBlockedUser> blockedUsers = new ArrayList<>();

    @OneToMany(mappedBy = "reporter", cascade = CascadeType.ALL)
    private List<ReportUser> reportsMade = new ArrayList<>();

    @OneToMany(mappedBy = "reported", cascade = CascadeType.ALL)
    private List<ReportUser> reportsReceived = new ArrayList<>();

    // Users this user follows
    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserFollow> following = new ArrayList<>();

    // Users who follow this user
    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserFollow> followers = new ArrayList<>();

    // User → Comments
    @OneToMany(mappedBy = "commentUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    // ReplyToUser → Comments
    @OneToMany(mappedBy = "replyToUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> replies = new ArrayList<>();

    // 1️⃣ One-to-Many: User -> Events Created
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> createdEvents = new ArrayList<>();

    @OneToMany(mappedBy = "quizUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizAttempt> quizAttempts = new ArrayList<>();

    @OneToMany(mappedBy = "courseCreator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Course> courses = new ArrayList<>();

    @OneToMany(mappedBy = "lessonCreator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lesson> lessons = new ArrayList<>();

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Group> createdBy = new ArrayList<>();

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<GroupUser> groupUsers = new ArrayList<>();

    @OneToMany(mappedBy = "eventUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventUser> events = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LessonProgress> lessonProgressUsers = new ArrayList<>();

    @OneToMany(mappedBy = "likeVideoUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikeVideo> likeVideoUsers = new ArrayList<>();

    // Sent Notification
    @OneToMany(mappedBy = "senderNotification", cascade = CascadeType.ALL)
    private List<Notification> sentNotification = new ArrayList<>();

    // Received Notification
    @OneToMany(mappedBy = "receiverNotification", cascade = CascadeType.ALL)
    private List<Notification> receivedNotification = new ArrayList<>();

    @OneToMany(mappedBy = "notificationPermissionUser", cascade = CascadeType.ALL)
    private List<NotificationPermission> notificationPermissionsUsers = new ArrayList<>();

    @OneToMany(mappedBy = "userTorSearch", cascade = CascadeType.ALL)
    private List<UserTopSearch> searchesUser = new ArrayList<>();

    @OneToMany(mappedBy = "bookmarkUser", cascade = CascadeType.ALL)
    private List<Bookmark> bookMarkUser = new ArrayList<>();

//    @OneToMany(mappedBy = "bannerCreator", cascade = CascadeType.ALL)
//    private List<Banner> bannerCreator = new ArrayList<>();

    @OneToOne(mappedBy = "communityCreatedUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Community communityCreatedUser;

    @OneToMany(mappedBy = "invitedBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> invitees = new ArrayList<>();

    @OneToMany(mappedBy = "conversationParticipantUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConversationParticipant> conversations = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeletedMessage> deletedMessages = new ArrayList<>();

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<RefreshToken> refreshTokenUser = new ArrayList<>();

}
