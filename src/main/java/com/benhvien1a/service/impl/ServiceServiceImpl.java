package com.benhvien1a.service.impl;

import com.benhvien1a.dto.ServiceDTO;
import com.benhvien1a.repository.ServiceRepository;
import com.benhvien1a.service.ServiceService;
import com.benhvien1a.util.SlugUtils;
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

        String slug = SlugUtils.generateSlug(request.getName());
        if (serviceRepository.existsBySlug(slug)) {
            logger.warn("Slug đã tồn tại: {}", slug);
            throw new RuntimeException("Slug đã tồn tại");
        }


        String thumbnailUrl = null;
        if (request.getThumbnail() != null && !request.getThumbnail().isEmpty()) {
            thumbnailUrl = cloudinaryService.uploadFile(request.getThumbnail());
        }

        Boolean active = request.getIsActive();

        com.benhvien1a.model.Service service = com.benhvien1a.model.Service.builder()
                .name(request.getName())
                .slug(slug)

                .description(request.getDescription())
                .thumbnail(thumbnailUrl)
                .isActive(active)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        serviceRepository.save(service);
        logger.info("Đã tạo dịch vụ với ID: {}", service.getId());
        return service;
    }

    @Override
    public com.benhvien1a.model.Service updateService(Long id, ServiceDTO request) {
        logger.info("Slug từ client: {}", request.getSlug());
        logger.info("Cập nhật dịch vụ với ID: {}", id);

        com.benhvien1a.model.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy dịch vụ với ID: {}", id);
                    return new RuntimeException("Không tìm thấy dịch vụ");
                });

        String newSlug;

        // Nếu người dùng sửa slug khác với slug hiện tại → dùng slug mới
        if (request.getSlug() != null && !request.getSlug().isBlank() && !request.getSlug().equals(service.getSlug())) {
            newSlug = request.getSlug();
        }
        // Nếu slug không đổi nhưng name thay đổi → generate slug từ name
        else if (!request.getName().equals(service.getName())) {
            newSlug = SlugUtils.generateSlug(request.getName());
        }
        // Không đổi gì → giữ nguyên slug cũ
        else {
            newSlug = service.getSlug();
        }

        if (!service.getSlug().equals(newSlug) && serviceRepository.existsBySlug(newSlug)) {
            logger.warn("Slug đã tồn tại: {}", newSlug);
            throw new RuntimeException("Slug đã tồn tại");
        }


        String thumbnailUrl = service.getThumbnail(); // giữ lại thumbnail cũ nếu không gửi mới
        if (request.getThumbnail() != null && !request.getThumbnail().isEmpty()) {
            thumbnailUrl = cloudinaryService.uploadFile(request.getThumbnail());
        }

        Boolean active = request.getIsActive();
        if( active == null) {
            active = service.isActive();
        }

        service.setName(request.getName());
        service.setSlug(newSlug);

        service.setDescription(request.getDescription());
        service.setThumbnail(thumbnailUrl);
        service.setActive(active);
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

        service.setActive(!service.isActive());
        service.setUpdatedAt(LocalDateTime.now());
        serviceRepository.save(service);
        logger.info("Đã ẩn dịch vụ với ID: {}", id);
    }


}
