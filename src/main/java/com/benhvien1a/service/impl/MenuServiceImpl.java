/*
 * @ (#) MenuServiceImpl.java 1.0 7/12/2025
 *
 * Copyright (c) 2025 IUH.All rights reserved
 */
package com.benhvien1a.service.impl;

import com.benhvien1a.dto.MenuDTO;
import com.benhvien1a.model.Menu;
import com.benhvien1a.repository.MenuRepository;
import com.benhvien1a.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * @description: Implementation of MenuService for managing Menu entities
 * @author: Nguyen Truong An
 * @date: 7/12/2025
 * @version: 1.0
 */
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {
    private static final Logger logger = LoggerFactory.getLogger(MenuServiceImpl.class);

    private final MenuRepository menuRepository;

    @Override
    public Menu createMenu(MenuDTO request) {
        logger.info("Tạo menu với tiêu đề: {}", request.getTitle());

        String slug = generateSlug(request.getTitle());
        if (menuRepository.existsBySlug(slug)) {
            logger.warn("Slug đã tồn tại: {}", slug);
            throw new RuntimeException("Slug đã tồn tại");
        }

        Menu menu = Menu.builder()
                .title(request.getTitle())
                .slug(slug)
                .url(request.getUrl())
                .orderIndex(request.getOrderIndex())
                .isActive(request.isActive())
                .build();

        return menuRepository.save(menu);
    }

    @Override
    public Menu updateMenu(Long id, MenuDTO request) {
        logger.info("Cập nhật menu ID: {}", id);

        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy menu với ID: {}", id);
                    return new RuntimeException("Không tìm thấy menu");
                });

        String newSlug = generateSlug(request.getTitle());
        if (!menu.getSlug().equals(newSlug) && menuRepository.existsBySlug(newSlug)) {
            logger.warn("Slug đã tồn tại: {}", newSlug);
            throw new RuntimeException("Slug đã tồn tại");
        }

        menu.setTitle(request.getTitle());
        menu.setSlug(newSlug);
        menu.setUrl(request.getUrl());
        menu.setOrderIndex(request.getOrderIndex());
        menu.setActive(request.isActive());

        return menuRepository.save(menu);
    }

    @Override
    public void deleteMenu(Long id) {
        logger.info("Xóa menu ID: {}", id);

        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy menu với ID: {}", id);
                    return new RuntimeException("Không tìm thấy menu");
                });

        menuRepository.delete(menu);
        logger.info("Xóa menu thành công: {}", id);
    }

    @Override
    public Menu getMenuById(Long id) {
        logger.info("Lấy menu ID: {}", id);
        return menuRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy menu với ID: {}", id);
                    return new RuntimeException("Không tìm thấy menu");
                });
    }

    @Override
    public Menu getMenuBySlug(String slug) {
        logger.info("Lấy menu với slug: {}", slug);
        return menuRepository.findBySlug(slug)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy menu với slug: {}", slug);
                    return new RuntimeException("Không tìm thấy menu với slug: " + slug);
                });
    }

    @Override
    public List<Menu> getAllMenus() {
        logger.info("Lấy tất cả menu");
        return menuRepository.findAll();
    }

    @Override
    public void hideMenu(Long id) {
        logger.info("Ẩn menu ID: {}", id);
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy menu với ID: {}", id);
                    return new RuntimeException("Không tìm thấy menu");
                });

        menu.setActive(false);
        menuRepository.save(menu);
        logger.info("Ẩn menu thành công: {}", id);
    }

    private String generateSlug(String title) {
        return title.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }
}