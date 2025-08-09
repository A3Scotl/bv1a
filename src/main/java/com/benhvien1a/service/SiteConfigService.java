package com.benhvien1a.service;

import com.benhvien1a.dto.SiteConfigDTO;
import com.benhvien1a.model.SiteConfig;

public interface SiteConfigService {
    SiteConfig getSiteConfig();
    SiteConfig updateSiteConfig(SiteConfigDTO request);
}
