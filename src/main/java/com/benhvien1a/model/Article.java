package com.benhvien1a.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "articles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(unique = true)
    private String slug;

    private String thumbnailUrl;

//    @ElementCollection
//    @CollectionTable(name = "article_images", joinColumns = @JoinColumn(name = "article_id"))
//    @Column(name = "image_url")
//    private List<String> imageUrls = new ArrayList<>(); // Multiple additional image URLs

    @Enumerated(EnumType.STRING)
    private ArticleStatus status = ArticleStatus.DRAFT;

    @ManyToOne
    private User author;




    private LocalDateTime publishAt;
    private LocalDateTime createAt = LocalDateTime.now();
    private LocalDateTime updateAt;

    private boolean isActive ;
}
