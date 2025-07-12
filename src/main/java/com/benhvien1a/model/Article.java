package com.benhvien1a.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    private String content;

    @Column(unique = true)
    private String slug;

    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    private ArticleStatus status = ArticleStatus.DRAFT;

    @ManyToOne
    private User author;

    @ManyToOne
    private Category category;



    private LocalDateTime publishAt;
    private LocalDateTime createAt = LocalDateTime.now();
    private LocalDateTime updateAt;

    private boolean isActive ;
}
