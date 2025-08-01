/*
 * @ (#) ArticleServiceImpl.java 1.0 7/12/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved
 */
package com.benhvien1a.service.impl;

import com.benhvien1a.dto.ArticleDTO;
import com.benhvien1a.model.*;
import com.benhvien1a.repository.ArticleRepository;
import com.benhvien1a.repository.UserRepository;
import com.benhvien1a.service.ArticleService;
import com.benhvien1a.util.SlugUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

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

    private final CloudinaryService cloudinaryService;


    @Override
    public Article createArticle(ArticleDTO request) {
        logger.info("Tạo bài viết với tiêu đề: {}", request.getTitle());

        String slug = SlugUtils.generateSlug(request.getTitle());
        if (articleRepository.existsBySlug(slug)) {
            logger.warn("Slug đã tồn tại: {}", slug);
            throw new RuntimeException("Slug đã tồn tại");
        }

        String thumbnailUrl = null;
        if (request.getThumbnail() != null && !request.getThumbnail().isEmpty()) {
            thumbnailUrl = cloudinaryService.uploadFile(request.getThumbnail());
        }



        ArticleStatus status = request.getStatus() != null ? request.getStatus() : ArticleStatus.DRAFT;
        LocalDateTime publishAt = null;
        if (status == ArticleStatus.PUBLISHED) {
            publishAt = LocalDateTime.now();
        }

        Article article = Article.builder()
                .title(request.getTitle())
                .slug(slug)
                .content(request.getContent())
                .type(ArticleType.valueOf(request.getType()))
                .thumbnailUrl(thumbnailUrl)
                .status(status)
                .publishAt(publishAt)
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        return articleRepository.save(article);
    }

    public Article updateArticle(Long id, ArticleDTO request) {
        logger.info("Cập nhật bài viết với ID: {} và tiêu đề: {}", id, request.getTitle());

        // Find the existing article
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy bài viết với ID: {}", id);
                    return new RuntimeException("Không tìm thấy bài viết");
                });

        // Update fields only if provided
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            String newSlug = SlugUtils.generateSlug(request.getTitle());
            if (!newSlug.equals(article.getSlug()) && articleRepository.existsBySlug(newSlug)) {
                logger.warn("Slug đã tồn tại: {}", newSlug);
                throw new RuntimeException("Slug đã tồn tại");
            }
            article.setTitle(request.getTitle());
            article.setSlug(newSlug);
        }

        if (request.getContent() != null) {
            article.setContent(request.getContent());
        }

        if (request.getThumbnail() != null && !request.getThumbnail().isEmpty()) {
            String thumbnailUrl = cloudinaryService.uploadFile(request.getThumbnail());
            article.setThumbnailUrl(thumbnailUrl);
        }

        // Update type if provided
        if (request.getType() != null) {
            try {
                ArticleType.valueOf(request.getType());
                article.setType(ArticleType.valueOf(request.getType()));
            } catch (IllegalArgumentException e) {
                logger.warn("Loại bài viết không hợp lệ: {}", request.getType());
                throw new RuntimeException("Loại bài viết không hợp lệ: " + request.getType());
            }
        }

        // Update status and publishAt
        if (request.getStatus() != null) {
            article.setStatus(request.getStatus());
            if (request.getStatus() == ArticleStatus.PUBLISHED && article.getPublishAt() == null) {
                article.setPublishAt(LocalDateTime.now());
            } else if (request.getStatus() != ArticleStatus.PUBLISHED && article.getStatus() == ArticleStatus.PUBLISHED) {
                article.setPublishAt(null);
            }
        }

        article.setUpdateAt(LocalDateTime.now());

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
    public List<Article> getAllActiveArticles() {
        logger.info("Lấy tất cả bài viết");
        return articleRepository.findAll().stream()
                .filter(article -> article.getStatus() == ArticleStatus.PUBLISHED)
                .toList();
    }

    @Override
    public void hideArticle(Long id) {
        logger.info("Ẩn/hiện bài viết ID: {}", id);
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy bài viết với ID: {}", id);
                    return new RuntimeException("Không tìm thấy bài viết");
                });

        ArticleStatus currentStatus = article.getStatus();
        if (currentStatus == ArticleStatus.PUBLISHED) {
            article.setStatus(ArticleStatus.ARCHIVED); // Ẩn bài viết
            logger.info("Bài viết chuyển sang ARCHIVED: {}", id);
        } else if (currentStatus == ArticleStatus.ARCHIVED) {
            article.setStatus(ArticleStatus.PUBLISHED); // Hiện lại bài viết nếu đang bị ẩn
            logger.info("Bài viết chuyển sang PUBLISHED: {}", id);
        } else {
            logger.warn("Không thể ẩn/hiện bài viết ở trạng thái {}: {}", currentStatus, id);
            throw new RuntimeException("Trạng thái bài viết không hợp lệ để ẩn/hiện");
        }

        article.setUpdateAt(LocalDateTime.now());
        articleRepository.save(article);
    }



    @Override
    public List<String> getAllArticleTypes() {
        return Arrays.stream(ArticleType.values())
                .map(Enum::name)
                .collect(toList());
    }

    @Override
    public List<Article> getArticlesByType(ArticleType type) {
        logger.info("Lấy tất cả bài viết với loại: {}", type);
        List<ArticleType> types = Arrays.asList(ArticleType.values());
        if (!types.contains(type)) {
            logger.error("Loại bài viết không hợp lệ: {}", type);
            throw new RuntimeException("Loại bài viết không hợp lệ");
        }
        return articleRepository.findByType(type);
    }

}