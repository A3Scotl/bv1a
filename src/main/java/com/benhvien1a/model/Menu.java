/*
 * @ (#) Menu.java 1.0 7/12/2025
 *
 * Copyright (c) 2025 IUH.All rights reserved
 */

package com.benhvien1a.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * @description
 * @author : Nguyen Truong An
 * @date : 7/12/2025
 * @version 1.0
 */
@Entity
@Table(name = "menus")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String slug;
    private String url;

    private Integer orderIndex;

    private boolean isActive;
}








