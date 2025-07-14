/*
 * @ (#) MenuController.java 1.0 7/12/2025
 *
 * Copyright (c) 2025 IUH.All rights reserved
 */
package com.benhvien1a.controller;

import com.benhvien1a.dto.response.ApiResponse;
import com.benhvien1a.dto.MenuDTO;
import com.benhvien1a.model.Menu;
import com.benhvien1a.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

/*
 * @description: Controller cho việc quản lý các menu
 * @author: Nguyen Truong An
 * @date: 7/12/2025
 * @version: 1.0
 */
@RestController
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
public class MenuController {
    private static final Logger logger = LoggerFactory.getLogger(MenuController.class);

    private final MenuService menuService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Menu>> createMenu(@Valid @RequestBody MenuDTO request) {
        logger.info("Nhận yêu cầu tạo menu: {}", request.getTitle());
        try {
            Menu menu = menuService.createMenu(request);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Tạo menu thành công",
                    menu,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/menus"
            ));
        } catch (Exception e) {
            logger.error("Tạo menu thất bại: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    false,
                    "Tạo menu thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/menus"
            ));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Menu>> updateMenu(@PathVariable Long id, @Valid @RequestBody MenuDTO request) {
        logger.info("Nhận yêu cầu cập nhật menu ID: {}", id);
        try {
            Menu menu = menuService.updateMenu(id, request);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Cập nhật menu thành công",
                    menu,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/menus/" + id
            ));
        } catch (Exception e) {
            logger.error("Cập nhật menu thất bại ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    false,
                    "Cập nhật menu thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/menus/" + id
            ));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteMenu(@PathVariable Long id) {
        logger.info("Nhận yêu cầu xóa menu ID: {}", id);
        try {
            menuService.deleteMenu(id);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Xóa menu thành công",
                    null,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/menus/" + id
            ));
        } catch (Exception e) {
            logger.error("Xóa menu thất bại ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    false,
                    "Xóa menu thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/menus/" + id
            ));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Menu>> getMenuById(@PathVariable Long id) {
        logger.info("Nhận yêu cầu lấy menu ID: {}", id);
        try {
            Menu menu = menuService.getMenuById(id);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Lấy menu thành công",
                    menu,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/menus/" + id
            ));
        } catch (Exception e) {
            logger.error("Lấy menu thất bại ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
                    false,
                    "Lấy menu thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/menus/" + id
            ));
        }
    }

    @GetMapping("/by-slug/{slug}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Menu>> getMenuBySlug(@PathVariable String slug) {
        logger.info("Nhận yêu cầu lấy menu với slug: {}", slug);
        try {
            Menu menu = menuService.getMenuBySlug(slug);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Lấy menu thành công",
                    menu,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/menus/by-slug/" + slug
            ));
        } catch (Exception e) {
            logger.error("Lấy menu thất bại với slug {}: {}", slug, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
                    false,
                    "Lấy menu thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/menus/by-slug/" + slug
            ));
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Menu>>> getAllMenus() {
        logger.info("Nhận yêu cầu lấy tất cả menu");
        try {
            List<Menu> menus = menuService.getAllMenus();
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Lấy danh sách menu thành công",
                    menus,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/menus"
            ));
        } catch (Exception e) {
            logger.error("Lấy danh sách menu thất bại: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                    false,
                    "Lấy danh sách menu thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/menus"
            ));
        }
    }

    @PatchMapping("/{id}/hide")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> hideMenu(@PathVariable Long id) {
        logger.info("Nhận yêu cầu ẩn menu ID: {}", id);
        try {
            menuService.hideMenu(id);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Ẩn menu thành công",
                    null,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/menus/" + id + "/hide"
            ));
        } catch (Exception e) {
            logger.error("Ẩn menu thất bại ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    false,
                    "Ẩn menu thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/menus/" + id + "/hide"
            ));
        }
    }
}