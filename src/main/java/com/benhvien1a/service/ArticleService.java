/*
 * @ (#) ArticleService.java 1.0 7/12/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved
 */
package com.benhvien1a.service;

import com.benhvien1a.dto.ArticleDTO;
import com.benhvien1a.model.Article;
import com.benhvien1a.model.ArticleType;
import com.benhvien1a.model.Doctor;

import java.util.List;

/*
 * @description: Service interface for managing Article entities
 * @author: Nguyen Truong An
 * @date: 7/12/2025
 * @version: 1.0
 */
public interface ArticleService {
    Article createArticle(ArticleDTO request);
    Article updateArticle(Long id, ArticleDTO request);
    void deleteArticle(Long id);
    Article getArticleById(Long id);
    Article getArticleBySlug(String slug);
    List<Article> getAllArticles();
    void hideArticle(Long id);
    List<Article> getAllActiveArticles();
    List<String> getAllArticleTypes();
    List<Article> getArticlesByType(ArticleType type);
}