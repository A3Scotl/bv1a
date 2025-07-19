package com.benhvien1a.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class DoctorDTO {
    private String fullName;
    private String description;
    private Long departmentId;
    private String position;
    private Boolean isActive;
    private MultipartFile avatarFile;
    private String avatarUrl;
    private String slug;

}
