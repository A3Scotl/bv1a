package com.benhvien1a.service.impl;

import com.benhvien1a.dto.ServiceDTO;
import com.benhvien1a.model.Category;
import com.benhvien1a.repository.CategoryRepository;
import com.benhvien1a.repository.ServiceRepository;
import com.benhvien1a.service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {
    private static final Logger logger = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    private final ServiceRepository serviceRepository;
    private final CategoryRepository categoryRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public List<com.benhvien1a.model.Service> getAllServices() {
        logger.info("Lấy tất cả dịch vụ");
        return serviceRepository.findAll();
    }

    @Override
    public com.benhvien1a.model.Service getServiceById(Long id) {
        logger.info("Lấy dịch vụ với ID: {}", id);
        return serviceRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy dịch vụ với ID: {}", id);
                    return new RuntimeException("Không tìm thấy dịch vụ");
                });
    }

    @Override
    public com.benhvien1a.model.Service createService(ServiceDTO request) {
        logger.info("Tạo dịch vụ với tên: {}", request.getName());

        String slug = generateSlug(request.getName());
        if (serviceRepository.existsBySlug(slug)) {
            logger.warn("Slug đã tồn tại: {}", slug);
            throw new RuntimeException("Slug đã tồn tại");
        }

        Category category = new Category();
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> {
                        logger.error("Không tìm thấy danh mục với ID: {}", request.getCategoryId());
                        return new RuntimeException("Không tìm thấy danh mục");
                    });
        }

        String thumbnailUrl = null;
        if (request.getThumbnail() != null && !request.getThumbnail().isEmpty()) {
            thumbnailUrl = cloudinaryService.uploadFile(request.getThumbnail());
        }

        com.benhvien1a.model.Service service = com.benhvien1a.model.Service.builder()
                .name(request.getName())
                .slug(slug)
                .category(category)
                .description(request.getDescription())
                .thumbnail(thumbnailUrl)
                .isActive(request.isActive())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        serviceRepository.save(service);
        logger.info("Đã tạo dịch vụ với ID: {}", service.getId());
        return service;
    }

    @Override
    public com.benhvien1a.model.Service updateService(Long id, ServiceDTO request) {
        logger.info("Cập nhật dịch vụ với ID: {}", id);

        com.benhvien1a.model.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy dịch vụ với ID: {}", id);
                    return new RuntimeException("Không tìm thấy dịch vụ");
                });

        String slug = generateSlug(request.getName());
        if (!service.getSlug().equals(slug) && serviceRepository.existsBySlug(slug)) {
            logger.warn("Slug đã tồn tại: {}", slug);
            throw new RuntimeException("Slug đã tồn tại");
        }

        Category category = new Category();
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> {
                        logger.error("Không tìm thấy danh mục với ID: {}", request.getCategoryId());
                        return new RuntimeException("Không tìm thấy danh mục");
                    });
        }

        String thumbnailUrl = null;
        if (request.getThumbnail() != null && !request.getThumbnail().isEmpty()) {
            thumbnailUrl = cloudinaryService.uploadFile(request.getThumbnail());
        }

        service.setName(request.getName());
        service.setSlug(slug);
        service.setCategory(category);
        service.setDescription(request.getDescription());
        service.setThumbnail(thumbnailUrl);
        service.setActive(request.isActive());
        service.setUpdatedAt(LocalDateTime.now());
        serviceRepository.save(service);
        logger.info("Đã cập nhật dịch vụ với ID: {}", id);
        return service;
    }

    @Override
    public void deleteService(Long id) {
        logger.info("Xóa dịch vụ với ID: {}", id);

        com.benhvien1a.model.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy dịch vụ với ID: {}", id);
                    return new RuntimeException("Không tìm thấy dịch vụ");
                });

        serviceRepository.delete(service);
        logger.info("Đã xóa dịch vụ với ID: {}", id);
    }

    @Override
    public com.benhvien1a.model.Service getServiceBySlug(String slug) {
        logger.info("Lấy dịch vụ với slug: {}", slug);
        return serviceRepository.findBySlug(slug)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy dịch vụ với slug: {}", slug);
                    return new RuntimeException("Không tìm thấy dịch vụ");
                });
    }

    @Override
    public List<com.benhvien1a.model.Service> getAllActiveServices() {
        logger.info("Lấy tất cả dịch vụ đang hoạt động");
        return serviceRepository.findAll().stream()
                .filter(com.benhvien1a.model.Service::isActive)
                .toList();
    }

    @Override
    public void hideService(Long id) {
        logger.info("Ẩn dịch vụ với ID: {}", id);
        com.benhvien1a.model.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy dịch vụ với ID: {}", id);
                    return new RuntimeException("Không tìm thấy dịch vụ");
                });

        service.setActive(false);
        service.setUpdatedAt(LocalDateTime.now());
        serviceRepository.save(service);
        logger.info("Đã ẩn dịch vụ với ID: {}", id);
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }
}
