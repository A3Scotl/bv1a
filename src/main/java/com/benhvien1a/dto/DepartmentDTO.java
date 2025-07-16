/*
 * @ (#) DepartmentDTO.java 1.0 7/12/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved
 */
package com.benhvien1a.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/*
 * @description: DTO for Department entity
 * @author: Nguyen Truong An
 * @date: 7/12/2025
 * @version: 1.0
 */
@Data
public class DepartmentDTO {
    @NotBlank(message = "Tên phòng ban là bắt buộc")
    private String name;
    private String description;

    private MultipartFile thumbnail;
    private String slug;
    private boolean isActive;
}