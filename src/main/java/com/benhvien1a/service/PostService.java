package com.benhvien1a.service;

import com.benhvien1a.dto.PostDTO;
import com.benhvien1a.model.Post;
import com.benhvien1a.model.PostStatus;
import com.benhvien1a.model.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {
    Page<Post> getAllPosts(Pageable pageable);
    Page<Post> getAllActivePosts(Pageable pageable);
    Page<Post> getFilteredPosts(String title, PostStatus status, PostType type, Pageable pageable);
    Page<Post> getPostsByType(PostType type, Pageable pageable);
    Post getPostById(Long id);
    Post getPostBySlug(String slug);
    Post createPost(PostDTO request);
    Post updatePost(Long id, PostDTO request);
    void deletePost(Long id);
    void hidePost(Long id);
    List<String> getAllPostTypes();
}