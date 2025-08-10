package com.benhvien1a.service.impl;

import com.benhvien1a.dto.PostDTO;
import com.benhvien1a.model.Post;
import com.benhvien1a.model.PostStatus;
import com.benhvien1a.model.PostType;
import com.benhvien1a.repository.PostRepository;
import com.benhvien1a.service.PostService;
import com.benhvien1a.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);
    private final PostRepository postRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional(readOnly = true)
    public Page<Post> getAllPosts(Pageable pageable) {
        logger.info("Fetching all posts with pagination");
        return postRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Post> getAllActivePosts(Pageable pageable) {
        logger.info("Fetching all active posts with pagination");
        return postRepository.findByStatus(PostStatus.PUBLIC, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Post> getFilteredPosts(String title, PostStatus status, PostType type, Pageable pageable) {
        logger.info("Fetching posts with title: {}, status: {}, type: {}", title, status, type);
        return postRepository.findByTitleAndStatusAndType(title, status, type, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Post> getPostsByType(PostType type, Pageable pageable) {
        logger.info("Fetching posts by type: {}", type);
        if (!List.of(PostType.values()).contains(type)) {
            throw new RuntimeException("Invalid post type: " + type);
        }
        return postRepository.findByType(type, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Post getPostById(Long id) {
        logger.info("Fetching post with ID: {}", id);
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + id));
    }

    @Override
    @Transactional
    public Post getPostBySlug(String slug) {
        logger.info("Fetching post with slug: {}", slug);

        Post post = postRepository.findBySlugAndStatus(slug,PostStatus.PUBLIC)
                .orElseThrow(() -> new RuntimeException("Post not found with slug: " + slug));

        // Tăng viewCount
        post.setViewCount(post.getViewCount() + 1);
        return post;
    }


    @Override
    @Transactional
    public Post createPost(PostDTO request) {
        logger.info("Creating post with title: {}", request.getTitle());
        String slug = SlugUtils.generateSlug(request.getTitle());
        if (postRepository.existsBySlug(slug)) {
            throw new RuntimeException("Slug already exists: " + slug);
        }

        String thumbnailUrl = request.getThumbnail() != null && !request.getThumbnail().isEmpty()
                ? cloudinaryService.uploadFile(request.getThumbnail())
                : null;

        PostStatus status = request.getStatus() != null ? request.getStatus() : PostStatus.PRIVATE;
        LocalDateTime publishAt = status == PostStatus.PUBLIC ? LocalDateTime.now() : null;

        Post post = Post.builder()
                .title(request.getTitle())
                .slug(slug)
                .content(request.getContent())
                .type(PostType.valueOf(request.getType()))
                .thumbnailUrl(thumbnailUrl)
                .status(status)
                .publishAt(publishAt)
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .viewCount(0)
                .build();

        return postRepository.save(post);
    }

    @Override
    @Transactional
    public Post updatePost(Long id, PostDTO request) {
        logger.info("Updating post with ID: {}", id);
        Post post = getPostById(id);

        String newSlug = post.getSlug();
        if (request.getTitle() != null && !request.getTitle().equals(post.getTitle())) {
            newSlug = SlugUtils.generateUniqueSlug(request.getTitle(), postRepository::existsBySlug);
        }

        String thumbnailUrl = request.getThumbnail() != null && !request.getThumbnail().isEmpty()
                ? cloudinaryService.uploadFile(request.getThumbnail())
                : post.getThumbnailUrl();

        post.setTitle(request.getTitle() != null ? request.getTitle() : post.getTitle());
        post.setSlug(newSlug);
        post.setContent(request.getContent() != null ? request.getContent() : post.getContent());
        post.setType(request.getType() != null ? PostType.valueOf(request.getType()) : post.getType());
        post.setThumbnailUrl(thumbnailUrl);
        if (request.getStatus() != null) {
            post.setStatus(request.getStatus());
            post.setPublishAt(request.getStatus() == PostStatus.PUBLIC && post.getPublishAt() == null
                    ? LocalDateTime.now()
                    : request.getStatus() != PostStatus.PUBLIC ? null : post.getPublishAt());
        }
        post.setUpdateAt(LocalDateTime.now());

        return postRepository.save(post);
    }

    @Override
    @Transactional
    public void deletePost(Long id) {
        logger.info("Deleting post with ID: {}", id);
        Post post = getPostById(id);
        postRepository.delete(post);
    }

    @Override
    @Transactional
    public void hidePost(Long id) {
        logger.info("Toggling status for post with ID: {}", id);
        Post post = getPostById(id);
        post.setStatus(post.getStatus() == PostStatus.PUBLIC ? PostStatus.PRIVATE : PostStatus.PUBLIC);
        post.setUpdateAt(LocalDateTime.now());
        post.setPublishAt(LocalDateTime.now());
        postRepository.save(post);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllPostTypes() {
        logger.info("Fetching all post types");
        return List.of(PostType.values()).stream()
                .map(Enum::name)
                .toList();
    }
}