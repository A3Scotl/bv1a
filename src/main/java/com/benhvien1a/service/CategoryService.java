/*
 * @ (#) CategoryService.java 1.0 7/12/2025
 *
 * Copyright (c) 2025 IUH.All rights reserved
 */
package com.benhvien1a.service;

import com.benhvien1a.dto.CategoryDTO;
import com.benhvien1a.model.Category;

import java.util.List;

/*
 * @description: Service interface for managing Category entities
 * @author: Nguyen Truong An
 * @date: 7/12/2025
 * @version: 1.0
 */
public interface CategoryService {
    Category createCategory(CategoryDTO request);
    Category updateCategory(Long id, CategoryDTO request);
    void deleteCategory(Long id);
    Category getCategoryById(Long id);
    Category getCategoryBySlug(String slug);
    List<Category> getAllCategories();
    void hideCategory(Long id);
}