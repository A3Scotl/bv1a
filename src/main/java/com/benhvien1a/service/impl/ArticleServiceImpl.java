/*
 * @ (#) ArticleServiceImpl.java 1.0 7/12/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved
 */
package com.benhvien1a.service.impl;

import com.benhvien1a.dto.ArticleDTO;
import com.benhvien1a.model.Article;
import com.benhvien1a.model.ArticleStatus;
import com.benhvien1a.model.Category;
import com.benhvien1a.model.User;
import com.benhvien1a.repository.ArticleRepository;
import com.benhvien1a.repository.CategoryRepository;
import com.benhvien1a.repository.UserRepository;
import com.benhvien1a.service.ArticleService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final CloudinaryService cloudinaryService;
    private final UserRepository userRepository;

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

        // Upload thumbnail to Cloudinary
        String thumbnailUrl = null;
        if (request.getThumbnail() != null && !request.getThumbnail().isEmpty()) {
            thumbnailUrl = cloudinaryService.uploadFile(request.getThumbnail());
        }

        // Get the authenticated user's email from the security context
        String userEmail = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.error("Không có thông tin xác thực trong SecurityContext");
            throw new RuntimeException("Không thể xác định người dùng: chưa đăng nhập");
        }

        Object principal = authentication.getPrincipal();
        logger.debug("Principal type: {}", principal.getClass().getName());
        logger.debug("Principal content: {}", principal);

        if (principal instanceof Jwt) {
            Jwt jwt = (Jwt) principal;
            userEmail = jwt.getClaimAsString("email");
            logger.debug("JWT claims: {}", jwt.getClaims());
        } else if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            userEmail = userDetails.getUsername();
        } else if (principal instanceof String) {
            userEmail = (String) principal;
        } else {
            logger.error("Lỗi xác thực người dùng: {}", principal.getClass().getName());
            throw new RuntimeException("Không thể xác định người dùng từ token");
        }

        if (userEmail == null) {
            logger.error("Không thể lấy email từ principal");
            throw new RuntimeException("Không thể xác định email người dùng");
        }

        // Fetch the User entity by email
        User author = userRepository.findByEmail(userEmail);
        if (author == null) {
            logger.error("Cannot find user with email: {}", userEmail);
            throw new RuntimeException("User not found");
        }

        Article article = Article.builder()
                .title(request.getTitle())
                .slug(slug)
                .content(request.getContent())
                .category(category)
                .thumbnailUrl(thumbnailUrl)
                .status(ArticleStatus.DRAFT)
                .author(author)
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .isActive(request.isActive())
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

        // Get the authenticated user's email from the security context
        String userEmail = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.error("Không có thông tin xác thực trong SecurityContext");
            throw new RuntimeException("Không thể xác định người dùng: chưa đăng nhập");
        }

        Object principal = authentication.getPrincipal();
        logger.debug("Principal type: {}", principal.getClass().getName());
        logger.debug("Principal content: {}", principal);

        if (principal instanceof Jwt) {
            Jwt jwt = (Jwt) principal;
            userEmail = jwt.getClaimAsString("email");
            logger.debug("JWT claims: {}", jwt.getClaims());
        } else if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            userEmail = userDetails.getUsername();
        } else if (principal instanceof String) {
            userEmail = (String) principal;
        } else {
            logger.error("Lỗi xác thực người dùng: {}", principal.getClass().getName());
            throw new RuntimeException("Không thể xác định người dùng từ token");
        }

        if (userEmail == null) {
            logger.error("Không thể lấy email từ principal");
            throw new RuntimeException("Không thể xác định email người dùng");
        }

        // Fetch the current user
        User currentUser = userRepository.findByEmail(userEmail);
        if (currentUser == null) {
            logger.error("Cannot find user with email: {}", userEmail);
            throw new RuntimeException("User not found");
        }

        // Authorization check: Only the original author or ADMIN can update
        if (!currentUser.getId().equals(article.getAuthor().getId()) &&
                !authentication.getAuthorities().stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            logger.error("Người dùng {} không có quyền cập nhật bài viết của tác giả {}", userEmail, article.getAuthor().getEmail());
            throw new RuntimeException("Không có quyền cập nhật bài viết");
        }

        // Update fields only if provided
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            String newSlug = generateSlug(request.getTitle());
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

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> {
                        logger.error("Không tìm thấy danh mục với ID: {}", request.getCategoryId());
                        return new RuntimeException("Không tìm thấy danh mục");
                    });
            article.setCategory(category);
        }

        if (request.getThumbnail() != null && !request.getThumbnail().isEmpty()) {
            String thumbnailUrl = cloudinaryService.uploadFile(request.getThumbnail());
            article.setThumbnailUrl(thumbnailUrl);
        }

        if (request.getStatus() != null) {
            article.setStatus(request.getStatus());
        }

        // Update status and publishAt
        if (request.getStatus() != null) {
            if (request.getStatus() == ArticleStatus.PUBLISHED && article.getPublishAt() == null) {
                article.setPublishAt(LocalDateTime.now());
            } else if (request.getStatus() != ArticleStatus.PUBLISHED && article.getStatus() == ArticleStatus.PUBLISHED) {
                article.setPublishAt(null);
            }
            article.setStatus(request.getStatus());
        }

        article.setActive(request.isActive());
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
    public void hideArticle(Long id) {
        logger.info("Ẩn bài viết ID: {}", id);
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy bài viết với ID: {}", id);
                    return new RuntimeException("Không tìm thấy bài viết");
                });

        article.setActive(!article.isActive());
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