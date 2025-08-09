package com.benhvien1a.service.impl;

import com.benhvien1a.dto.SiteConfigDTO;
import com.benhvien1a.model.SiteConfig;
import com.benhvien1a.repository.SiteConfigRepository;
import com.benhvien1a.service.impl.CloudinaryService;
import com.benhvien1a.service.SiteConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteConfigServiceImpl implements SiteConfigService {

    private final SiteConfigRepository siteConfigRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public SiteConfig getSiteConfig() {
        return siteConfigRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("SiteConfig not found"));
    }

    @Override
    @Transactional
    public SiteConfig updateSiteConfig(SiteConfigDTO request) {
        SiteConfig config = siteConfigRepository.findById(1L).orElse(new SiteConfig());
        config.setId(1L);

        config.setAddress(request.getAddress());
        config.setPhoneNumber(request.getPhoneNumber());
        config.setEmail(request.getEmail());
        config.setWorkingHours(request.getWorkingHours());

        // Xử lý logo
        if (request.getLogoFile() != null && !request.getLogoFile().isEmpty()) {
            String uploadedLogoUrl = cloudinaryService.uploadFile(request.getLogoFile());
            config.setLogoUrl(uploadedLogoUrl);
        } else if (request.getLogoUrl() != null) {
            config.setLogoUrl(request.getLogoUrl());
        }

        // Xử lý banner
        List<String> banners = new ArrayList<>();
        if (request.getBannerFiles() != null && !request.getBannerFiles().isEmpty()) {
            for (var file : request.getBannerFiles()) {
                if (file != null && !file.isEmpty()) {
                    banners.add(cloudinaryService.uploadFile(file));
                }
            }
        } else if (request.getBannerUrls() != null) {
            banners.addAll(request.getBannerUrls());
        }
        config.setBannerImages(banners);

        if (config.getCreateAt() == null) {
            config.setCreateAt(LocalDateTime.now());
        }
        config.setUpdateAt(LocalDateTime.now());

        return siteConfigRepository.save(config);
    }
}
