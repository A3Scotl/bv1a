package com.benhvien1a.repository;

import com.benhvien1a.model.Post;
import com.benhvien1a.model.PostStatus;
import com.benhvien1a.model.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    boolean existsBySlug(String slug);
    Optional<Post> findBySlug(String slug);

    Page<Post> findAll(Pageable pageable);
    Page<Post> findByStatus(PostStatus status, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE (:title IS NULL OR p.title LIKE %:title%) AND (:status IS NULL OR p.status = :status) AND (:type IS NULL OR p.type = :type)")
    Page<Post> findByTitleAndStatusAndType(String title, PostStatus status, PostType type, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.type = :type")
    Page<Post> findByType(PostType type, Pageable pageable);
}