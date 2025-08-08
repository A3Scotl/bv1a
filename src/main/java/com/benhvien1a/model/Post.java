package com.benhvien1a.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "posts", indexes = {
        @Index(name = "idx_post_slug", columnList = "slug", unique = true),
        @Index(name = "idx_post_status", columnList = "status"),
        @Index(name = "idx_post_type", columnList = "type"),
        @Index(name = "idx_post_title", columnList = "title"),
        @Index(name = "idx_post_publish_at", columnList = "publishAt")
})
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String slug;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private PostType type;
    private String thumbnailUrl;
    @Enumerated(EnumType.STRING)
    private PostStatus status;
    private int viewCount;
    private LocalDateTime publishAt;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;


}
