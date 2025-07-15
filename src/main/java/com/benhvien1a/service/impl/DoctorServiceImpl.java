package com.benhvien1a.service.impl;

import com.benhvien1a.dto.DoctorDTO;
import com.benhvien1a.model.Department;
import com.benhvien1a.model.Doctor;
import com.benhvien1a.repository.DepartmentRepository;
import com.benhvien1a.repository.DoctorRepository;
import com.benhvien1a.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private static final Logger logger = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public List<Doctor> getAllDoctors() {
        logger.info("Lấy tất cả bac si");
        return doctorRepository.findAll();
    }

    @Override
    public List<Doctor> getAllActiveDoctors() {
        logger.info("Lấy tất cả bac si đang hoạt động");
        return doctorRepository.findAll().stream()
                .filter(Doctor::isActive)
                .toList();
    }

    @Override
    public Doctor creaeDoctor(DoctorDTO request) {
        logger.info("Tạo bac si với tên: {}", request.getFullName());

        String slug = generateSlug(request.getFullName());
        if (doctorRepository.existsBySlug(slug)) {
            logger.warn("Slug đã tồn tại: {}", slug);
            throw new RuntimeException("Slug đã tồn tại");
        }

        Department department = new Department();
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> {
                        logger.error("Không tìm thấy phòng ban với ID: {}", request.getDepartmentId());
                        return new RuntimeException("Không tìm thấy phòng ban");
                    });
        }

        String avatarUrl = null;
        if (request.getAvatarUrl() != null && !request.getAvatarUrl().isEmpty()) {
            avatarUrl = cloudinaryService.uploadFile(request.getAvatarUrl());
        }

        Doctor doctor = Doctor.builder()
                .fullName(request.getFullName())
                .slug(slug)
                .department(department)
                .description(request.getDescription())
                .avatarUrl(avatarUrl)
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .isActive(request.isActive())
                .build();

        return doctorRepository.save(doctor);
    }

    @Override
    public Doctor updateDoctor(Long id, DoctorDTO request) {
        logger.info("Cập nhật bac si với ID: {}", id);

        String avatarUrl = null;
        if (request.getAvatarUrl() != null && !request.getAvatarUrl().isEmpty()) {
            avatarUrl = cloudinaryService.uploadFile(request.getAvatarUrl());
        }

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy bac si với ID: {}", id);
                    return new RuntimeException("Không tìm thấy bac si");
                });

        String newSlug = generateSlug(request.getFullName());
        if (!doctor.getSlug().equals(newSlug) && doctorRepository.existsBySlug(newSlug)) {
            logger.warn("Slug đã tồn tại: {}", newSlug);
            throw new RuntimeException("Slug đã tồn tại");
        }

        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> {
                        logger.error("Không tìm thấy phòng ban với ID: {}", request.getDepartmentId());
                        return new RuntimeException("Không tìm thấy phòng ban");
                    });
        }

        doctor.setFullName(request.getFullName());
        doctor.setSlug(newSlug);
        doctor.setDepartment(department);
        doctor.setDescription(request.getDescription());
        doctor.setAvatarUrl(avatarUrl);
        doctor.setUpdateAt(LocalDateTime.now());
        doctor.setActive(request.isActive());

        return doctorRepository.save(doctor);

    }

    @Override
    public void deleteDoctor(Long id) {
        logger.info("Xóa bac si với ID: {}", id);

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy bac si với ID: {}", id);
                    return new RuntimeException("Không tìm thấy bac si");
                });

        doctorRepository.delete(doctor);
        logger.info("Đã xóa bac si với ID: {}", id);
    }

    @Override
    public Doctor getDoctorById(Long id) {
        logger.info("Lấy bac si với ID: {}", id);

        return doctorRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy bac si với ID: {}", id);
                    return new RuntimeException("Không tìm thấy bac si");
                });
    }

    @Override
    public Doctor getDoctorBySlug(String slug) {
        logger.info("Lấy bac si với slug: {}", slug);

        return doctorRepository.findBySlug(slug)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy bac si với slug: {}", slug);
                    return new RuntimeException("Không tìm thấy bac si");
                });
    }

    @Override
    public void hideDoctor(Long id) {
        logger.info("Ẩn bac si với ID: {}", id);
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy bac si với ID: {}", id);
                    return new RuntimeException("Không tìm thấy bac si");
                });

        doctor.setActive(!doctor.isActive());
        doctor.setUpdateAt(LocalDateTime.now());
        doctorRepository.save(doctor);
        logger.info("Đã ẩn bac si với ID: {}", id);
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }
}
