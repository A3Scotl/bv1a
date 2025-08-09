package com.benhvien1a.controller;

import com.benhvien1a.dto.SiteConfigDTO;
import com.benhvien1a.model.SiteConfig;
import com.benhvien1a.service.SiteConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartException;

@RestController
@RequestMapping("/api/v1/site-config")
@RequiredArgsConstructor
public class SiteConfigController {

    private final SiteConfigService siteConfigService;

    @GetMapping
    public ResponseEntity<SiteConfig> getSiteConfig() {
        return ResponseEntity.ok(siteConfigService.getSiteConfig());
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<SiteConfig> updateSiteConfig(@ModelAttribute SiteConfigDTO request) {
        try {
            return ResponseEntity.ok(siteConfigService.updateSiteConfig(request));
        } catch (MultipartException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
