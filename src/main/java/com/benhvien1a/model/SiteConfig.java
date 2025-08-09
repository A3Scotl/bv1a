package com.benhvien1a.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "site_config")
public class SiteConfig {

    @Id
    private Long id = 1L;

    private String address;
    private String phoneNumber;
    private String email;
    private String workingHours;
    private String logoUrl;

    @ElementCollection
    @CollectionTable(name = "site_banners", joinColumns = @JoinColumn(name = "site_config_id"))
    @Column(name = "image_url")
    private List<String> bannerImages;

    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
