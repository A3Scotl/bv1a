/*
 * @ (#) CategoryDTO.java 1.0 7/12/2025
 *
 * Copyright (c) 2025 IUH.All rights reserved
 */
package com.benhvien1a.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/*
 * @description: DTO for Category entity
 * @author: Nguyen Truong An
 * @date: 7/12/2025
 * @version: 1.0
 */
@Data
public class CategoryDTO {
    @NotBlank(message = "Name is mandatory")
    private String name;

    private boolean isActive;
}