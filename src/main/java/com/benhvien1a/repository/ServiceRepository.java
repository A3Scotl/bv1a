/*
 * @ (#) ServiceRepository.java 1.0 7/12/2025
 *
 * Copyright (c) 2025 IUH.All rights reserved
 */
package com.benhvien1a.repository;

import com.benhvien1a.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/*
 * @description: Repository for managing Service entities
 * @author: Nguyen Truong An
 * @date: 7/12/2025
 * @version: 1.0
 */
@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    boolean existsBySlug(String slug);
    Optional<Service> findBySlug(String slug);

}