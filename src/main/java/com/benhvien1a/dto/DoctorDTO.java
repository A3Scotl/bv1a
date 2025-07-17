package com.benhvien1a.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class DoctorDTO {
    @NotBlank(message = "Ten la bat buoc")
    private String fullName;
    private MultipartFile avatarUrl;
    private String description;
    private String slug;
    private Long departmentId;
    private boolean isActive;
    private String position;
}
