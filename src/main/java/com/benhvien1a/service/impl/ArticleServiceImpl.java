/*
 * @ (#) ArticleServiceImpl.java 1.0 7/12/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved
 */
package com.benhvien1a.service.impl;

import com.benhvien1a.dto.ArticleDTO;
import com.benhvien1a.model.Article;
import com.benhvien1a.model.Category;
import com.benhvien1a.repository.ArticleRepository;
import com.benhvien1a.repository.CategoryRepository;
import com.benhvien1a.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/*
 * @description: Implementation of ArticleService for managing Article entities
 * @author: Nguyen Truong An
 * @date: 7/12/2025
 * @version: 1.0
 */
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {
    private static final Logger logger = LoggerFactory.getLogger(ArticleServiceImpl.class);

    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Article createArticle(ArticleDTO request) {
        logger.info("Tạo bài viết với tiêu đề: {}", request.getTitle());

        String slug = generateSlug(request.getTitle());
        if (articleRepository.existsBySlug(slug)) {
            logger.warn("Slug đã tồn tại: {}", slug);
            throw new RuntimeException("Slug đã tồn tại");
        }

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> {
                        logger.error("Không tìm thấy danh mục với ID: {}", request.getCategoryId());
                        return new RuntimeException("Không tìm thấy danh mục");
                    });
        }

        Article article = Article.builder()
                .title(request.getTitle())
                .slug(slug)
                .content(request.getContent())
                .category(category)
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .isActive(request.isActive())
                .build();

        return articleRepository.save(article);
    }

    @Override
    public Article updateArticle(Long id, ArticleDTO request) {
        logger.info("Cập nhật bài viết ID: {}", id);

        Article article = articleRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy bài viết với ID: {}", id);
                    return new RuntimeException("Không tìm thấy bài viết");
                });

        String newSlug = generateSlug(request.getTitle());
        if (!article.getSlug().equals(newSlug) && articleRepository.existsBySlug(newSlug)) {
            logger.warn("Slug đã tồn tại: {}", newSlug);
            throw new RuntimeException("Slug đã tồn tại");
        }

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> {
                        logger.error("Không tìm thấy danh mục với ID: {}", request.getCategoryId());
                        return new RuntimeException("Không tìm thấy danh mục");
                    });
        }

        article.setTitle(request.getTitle());
        article.setSlug(newSlug);
        article.setContent(request.getContent());
        article.setCategory(category);
        article.setUpdateAt(LocalDateTime.now());
        article.setActive(request.isActive());

        return articleRepository.save(article);
    }

    @Override
    public void deleteArticle(Long id) {
        logger.info("Xóa bài viết ID: {}", id);

        Article article = articleRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy bài viết với ID: {}", id);
                    return new RuntimeException("Không tìm thấy bài viết");
                });

        articleRepository.delete(article);
        logger.info("Xóa bài viết thành công: {}", id);
    }

    @Override
    public Article getArticleById(Long id) {
        logger.info("Lấy bài viết ID: {}", id);
        return articleRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy bài viết với ID: {}", id);
                    return new RuntimeException("Không tìm thấy bài viết");
                });
    }

    @Override
    public Article getArticleBySlug(String slug) {
        logger.info("Lấy bài viết với slug: {}", slug);
        return articleRepository.findBySlug(slug)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy bài viết với slug: {}", slug);
                    return new RuntimeException("Không tìm thấy bài viết với slug: " + slug);
                });
    }

    @Override
    public List<Article> getAllArticles() {
        logger.info("Lấy tất cả bài viết");
        return articleRepository.findAll();
    }

    @Override
    public void hideArticle(Long id) {
        logger.info("Ẩn bài viết ID: {}", id);
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy bài viết với ID: {}", id);
                    return new RuntimeException("Không tìm thấy bài viết");
                });

        article.setActive(false);
        article.setUpdateAt(LocalDateTime.now());
        articleRepository.save(article);
        logger.info("Ẩn bài viết thành công: {}", id);
    }

    private String generateSlug(String title) {
        return title.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }
}