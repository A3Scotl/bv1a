package com.benhvien1a.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class SiteConfigDTO {
    private String address;
    private String phoneNumber;
    private String email;
    private String workingHours;
    private MultipartFile logoFile;
    private String logoUrl;
    private List<MultipartFile> bannerFiles;
    private List<String> bannerUrls;
}
