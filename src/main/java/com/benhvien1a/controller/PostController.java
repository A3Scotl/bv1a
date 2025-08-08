package com.benhvien1a.controller;

import com.benhvien1a.dto.PostDTO;
import com.benhvien1a.response.ApiResponse;
import com.benhvien1a.model.Post;
import com.benhvien1a.model.PostStatus;
import com.benhvien1a.model.PostType;
import com.benhvien1a.service.PostService;
import com.benhvien1a.util.ApiResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);
    private final PostService postService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Post>> createPost(@Valid @ModelAttribute PostDTO request) {
        logger.info("Received request to create post: {}", request.getTitle());
        try {
            Post post = postService.createPost(request);
            return ApiResponseUtil.buildResponse(true, "Post created successfully", post, "/api/v1/posts");
        } catch (Exception e) {
            logger.error("Failed to create post: {}", e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to create post: " + e.getMessage(), e.getMessage(), "/api/v1/posts");
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Post>> updatePost(@PathVariable Long id, @Valid @ModelAttribute PostDTO request) {
        logger.info("Received request to update post with ID: {}", id);
        try {
            Post post = postService.updatePost(id, request);
            return ApiResponseUtil.buildResponse(true, "Post updated successfully", post, "/api/v1/posts/" + id);
        } catch (Exception e) {
            logger.error("Failed to update post: {}", e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to update post: " + e.getMessage(), e.getMessage(), "/api/v1/posts/" + id);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable Long id) {
        logger.info("Received request to delete post with ID: {}", id);
        try {
            postService.deletePost(id);
            return ApiResponseUtil.buildResponse(true, "Post deleted successfully", null, "/api/v1/posts/" + id);
        } catch (Exception e) {
            logger.error("Failed to delete post: {}", e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to delete post: " + e.getMessage(), e.getMessage(), "/api/v1/posts/" + id);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Post>> getPostById(@PathVariable Long id) {
        logger.info("Received request to get post with ID: {}", id);
        try {
            Post post = postService.getPostById(id);
            return ApiResponseUtil.buildResponse(true, "Post retrieved successfully", post, "/api/v1/posts/" + id);
        } catch (Exception e) {
            logger.error("Failed to retrieve post: {}", e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.NOT_FOUND, "Failed to retrieve post: " + e.getMessage(), e.getMessage(), "/api/v1/posts/" + id);
        }
    }

    @GetMapping("/by-slug/{slug}")
    public ResponseEntity<ApiResponse<Post>> getPostBySlug(@PathVariable String slug) {
        logger.info("Received request to get post with slug: {}", slug);
        try {
            Post post = postService.getPostBySlug(slug);
            return ApiResponseUtil.buildResponse(true, "Post retrieved successfully", post, "/api/v1/posts/by-slug/" + slug);
        } catch (Exception e) {
            logger.error("Failed to retrieve post: {}", e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.NOT_FOUND, "Failed to retrieve post: " + e.getMessage(), e.getMessage(), "/api/v1/posts/by-slug/" + slug);
        }
    }

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<Page<Post>>> getAllActivePosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Received request to get all active posts with page: {}, size: {}", page, size);
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Post> posts = postService.getAllActivePosts(pageable);
            return ApiResponseUtil.buildResponse(true, "All active posts retrieved successfully", posts, "/api/v1/posts/public");
        } catch (Exception e) {
            logger.error("Failed to retrieve active posts: {}", e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve active posts: " + e.getMessage(), e.getMessage(), "/api/v1/posts/public");
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Page<Post>>> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) PostStatus status,
            @RequestParam(required = false) PostType type) {
        logger.info("Received request to get all posts with page: {}, size: {}, title: {}, status: {}, type: {}", page, size, title, status, type);
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Post> posts = title != null || status != null || type != null
                    ? postService.getFilteredPosts(title, status, type, pageable)
                    : postService.getAllPosts(pageable);
            return ApiResponseUtil.buildResponse(true, "All posts retrieved successfully", posts, "/api/v1/posts");
        } catch (Exception e) {
            logger.error("Failed to retrieve all posts: {}", e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve all posts: " + e.getMessage(), e.getMessage(), "/api/v1/posts");
        }
    }

    @PatchMapping("/{id}/hide")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Void>> hidePost(@PathVariable Long id) {
        logger.info("Received request to toggle status for post with ID: {}", id);
        try {
            postService.hidePost(id);
            return ApiResponseUtil.buildResponse(true, "Post status toggled successfully", null, "/api/v1/posts/" + id + "/hide");
        } catch (Exception e) {
            logger.error("Failed to toggle post status: {}", e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to toggle post status: " + e.getMessage(), e.getMessage(), "/api/v1/posts/" + id + "/hide");
        }
    }

    @GetMapping("/post-types")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<List<String>>> getAllPostTypes() {
        logger.info("Received request to get all post types");
        try {
            List<String> postTypes = postService.getAllPostTypes();
            return ApiResponseUtil.buildResponse(true, "All post types retrieved successfully", postTypes, "/api/v1/posts/post-types");
        } catch (Exception e) {
            logger.error("Failed to retrieve post types: {}", e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve post types: " + e.getMessage(), e.getMessage(), "/api/v1/posts/post-types");
        }
    }

    @GetMapping("/by-type/{type}")
    public ResponseEntity<ApiResponse<Page<Post>>> getPostsByType(
            @PathVariable PostType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Received request to get posts by type: {} with page: {}, size: {}", type, page, size);
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Post> posts = postService.getPostsByType(type, pageable);
            return ApiResponseUtil.buildResponse(true, "Posts by type retrieved successfully", posts, "/api/v1/posts/by-type/" + type);
        } catch (Exception e) {
            logger.error("Failed to retrieve posts by type: {}", e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve posts by type: " + e.getMessage(), e.getMessage(), "/api/v1/posts/by-type/" + type);
        }
    }
}