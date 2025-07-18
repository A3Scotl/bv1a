/*
 * @ (#) ArticleRepository.java 1.0 7/12/2025
 *
 * Copyright (c) 2025 IUH.All rights reserved
 */
package com.benhvien1a.repository;

import com.benhvien1a.model.Article;
import com.benhvien1a.model.ArticleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/*
 * @description: Repository for managing Article entities
 * @author: Nguyen Truong An
 * @date: 7/12/2025
 * @version: 1.0
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    boolean existsBySlug(String slug);
    Optional<Article> findBySlug(String slug);

    // get all articles by type
    @Query("SELECT a FROM Article a WHERE a.type = ?1")
    List<Article> findByType(ArticleType type);

}