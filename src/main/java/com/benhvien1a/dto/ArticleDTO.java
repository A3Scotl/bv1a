/*
 * @ (#) ArticleDTO.java 1.0 7/12/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved
 */
package com.benhvien1a.dto;

import com.benhvien1a.model.ArticleStatus;
import com.benhvien1a.model.ArticleType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/*
 * @description: DTO for Article entity
 * @author: Nguyen Truong An
 * @date: 7/12/2025
 * @version: 1.0
 */
@Data
public class ArticleDTO {
    @NotBlank(message = "Tiêu đề là bắt buộc")
    private String title;

    private String content;

    private ArticleType type;


    private MultipartFile thumbnail;

    private ArticleStatus status;

//    private List<MultipartFile> images; // Multiple additional images
}