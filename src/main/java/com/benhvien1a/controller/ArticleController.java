/*
 * @ (#) ArticleController.java 1.0 7/12/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved
 */
package com.benhvien1a.controller;

import com.benhvien1a.dto.response.ApiResponse;
import com.benhvien1a.dto.ArticleDTO;
import com.benhvien1a.model.Article;
import com.benhvien1a.service.ArticleService;
import com.benhvien1a.service.impl.CloudinaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

/*
 * @description: Controller cho việc quản lý các bài viết
 * @author: Nguyen Truong An
 * @date: 7/12/2025
 * @version: 1.0
 */
@RestController
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
public class ArticleController {
    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);

    private final ArticleService articleService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Article>> createArticle(@Valid @ModelAttribute ArticleDTO request) {
        logger.info("Nhận yêu cầu tạo bài viết: {}", request.getTitle());
        try {
            Article article = articleService.createArticle(request);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Tạo bài viết thành công",
                    article,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/articles"
            ));
        } catch (Exception e) {
            logger.error("Tạo bài viết thất bại: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    false,
                    "Tạo bài viết thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/articles"
            ));
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Article>> updateArticle(@PathVariable Long id, @Valid @ModelAttribute ArticleDTO request) {
        logger.info("Nhận yêu cầu cập nhật bài viết ID: {}", id);
        try {
            Article article = articleService.updateArticle(id, request);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Cập nhật bài viết thành công",
                    article,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/articles/" + id
            ));
        } catch (Exception e) {
            logger.error("Cập nhật bài viết thất bại ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    false,
                    "Cập nhật bài viết thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/articles/" + id
            ));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Void>> deleteArticle(@PathVariable Long id) {
        logger.info("Nhận yêu cầu xóa bài viết ID: {}", id);
        try {
            articleService.deleteArticle(id);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Xóa bài viết thành công",
                    null,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/articles/" + id
            ));
        } catch (Exception e) {
            logger.error("Xóa bài viết thất bại ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    false,
                    "Xóa bài viết thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/articles/" + id
            ));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Article>> getArticleById(@PathVariable Long id) {
        logger.info("Nhận yêu cầu lấy bài viết ID: {}", id);
        try {
            Article article = articleService.getArticleById(id);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Lấy bài viết thành công",
                    article,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/articles/" + id
            ));
        } catch (Exception e) {
            logger.error("Lấy bài viết thất bại ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
                    false,
                    "Lấy bài viết thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/articles/" + id
            ));
        }
    }

    @GetMapping("/by-slug/{slug}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Article>> getArticleBySlug(@PathVariable String slug) {
        logger.info("Nhận yêu cầu lấy bài viết với slug: {}", slug);
        try {
            Article article = articleService.getArticleBySlug(slug);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Lấy bài viết thành công",
                    article,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/articles/by-slug/" + slug
            ));
        } catch (Exception e) {
            logger.error("Lấy bài viết thất bại với slug {}: {}", slug, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
                    false,
                    "Lấy bài viết thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/articles/by-slug/" + slug
            ));
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<List<Article>>> getAllArticles() {
        logger.info("Nhận yêu cầu lấy tất cả bài viết");
        try {
            List<Article> articles = articleService.getAllArticles();
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Lấy danh sách bài viết thành công",
                    articles,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/articles"
            ));
        } catch (Exception e) {
            logger.error("Lấy danh sách bài viết thất bại: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                    false,
                    "Lấy danh sách bài viết thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/articles"
            ));
        }
    }

    @PatchMapping("/{id}/hide")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Void>> hideArticle(@PathVariable Long id) {
        logger.info("Nhận yêu cầu Thay đổi trạng thái bài viết ID: {}", id);
        try {
            articleService.hideArticle(id);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Thay đổi trạng thái bài viết thành công",
                    null,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/articles/" + id + "/hide"
            ));
        } catch (Exception e) {
            logger.error("Thay đổi trạng thái bài viết thất bại ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    false,
                    "Thay đổi trạng thái bài viết thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/articles/" + id + "/hide"
            ));
        }
    }
}