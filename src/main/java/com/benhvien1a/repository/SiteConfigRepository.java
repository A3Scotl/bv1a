/*
 * @ (#) SiteConfigRepository.java 1.0 8/9/2025
 *
 * Copyright (c) 2025 IUH.All rights reserved
 */
package com.benhvien1a.repository;

import com.benhvien1a.model.SiteConfig;
import org.springframework.data.jpa.repository.JpaRepository;

/*
 * @description
 * @author : Nguyen Truong An
 * @date : 8/9/2025
 * @version 1.0
 */
public interface SiteConfigRepository extends JpaRepository<SiteConfig, Long> {
}
