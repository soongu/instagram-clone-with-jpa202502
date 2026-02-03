package com.example.instagramclone.domain.member.entity;

import com.example.instagramclone.domain.comment.entity.Comment;
import com.example.instagramclone.domain.follow.entity.Follow;
import com.example.instagramclone.domain.like.entity.PostLike;
import com.example.instagramclone.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_email", columnList = "email"),
                @Index(name = "idx_phone", columnList = "phone"),
                @Index(name = "idx_username", columnList = "username")
        }
)
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"posts", "comments", "likes", "followers", "following"})
@EqualsAndHashCode
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @Setter
    @Column(length = 100)
    private String password;

    @Column(unique = true, length = 100)
    private String email;

    @Column(unique = true, length = 20)
    private String phone;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 255)
    private String profileImageUrl;

    @Column(nullable = false, length = 20)
    private String role = "ROLE_USER";

    @Column(length = 255)
    private String refreshToken;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @OneToMany(mappedBy = "member")
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<PostLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "toMember")
    private List<Follow> followers = new ArrayList<>();

    @OneToMany(mappedBy = "fromMember")
    private List<Follow> following = new ArrayList<>();

    @Builder
    private Member(String username, String password, String email,
                   String phone, String name, String profileImageUrl) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
    }
}
