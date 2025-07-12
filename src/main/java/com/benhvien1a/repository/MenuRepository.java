/*
 * @ (#) MenuRepository.java 1.0 7/12/2025
 *
 * Copyright (c) 2025 IUH.All rights reserved
 */
package com.benhvien1a.repository;

import com.benhvien1a.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/*
 * @description: Repository for managing Menu entities
 * @author: Nguyen Truong An
 * @date: 7/12/2025
 * @version: 1.0
 */
@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    boolean existsBySlug(String slug);
    Optional<Menu> findBySlug(String slug);
}