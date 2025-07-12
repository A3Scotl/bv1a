/*
 * @ (#) MenuService.java 1.0 7/12/2025
 *
 * Copyright (c) 2025 IUH.All rights reserved
 */
package com.benhvien1a.service;

import com.benhvien1a.dto.MenuDTO;
import com.benhvien1a.model.Menu;

import java.util.List;

/*
 * @description: Service interface for managing Menu entities
 * @author: Nguyen Truong An
 * @date: 7/12/2025
 * @version: 1.0
 */
public interface MenuService {
    Menu createMenu(MenuDTO request);
    Menu updateMenu(Long id, MenuDTO request);
    void deleteMenu(Long id);
    Menu getMenuById(Long id);
    Menu getMenuBySlug(String slug);
    List<Menu> getAllMenus();
    void hideMenu(Long id);
}