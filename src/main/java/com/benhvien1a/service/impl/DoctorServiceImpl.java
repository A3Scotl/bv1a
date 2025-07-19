package com.benhvien1a.service.impl;

import com.benhvien1a.dto.DoctorDTO;
import com.benhvien1a.model.Department;
import com.benhvien1a.model.Doctor;
import com.benhvien1a.model.Position;
import com.benhvien1a.repository.DepartmentRepository;
import com.benhvien1a.repository.DoctorRepository;
import com.benhvien1a.service.DoctorService;
import com.benhvien1a.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private static final Logger logger = LoggerFactory.getLogger(DoctorServiceImpl.class);

    private final DoctorRepository doctorRepository;
    private final DepartmentRepository departmentRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public List<Doctor> getAllDoctors() {
        logger.info("Lấy tất cả bác sĩ");
        return doctorRepository.findAll();
    }

    @Override
    public List<Doctor> getDotorsByDepartmentSlug(String departmentSlug) {
        logger.info("Lấy tất cả bác sĩ theo phòng ban slug: {}", departmentSlug);
        return doctorRepository.findAll().stream()
                .filter(doctor ->
                        doctor.getDepartment() != null &&
                                departmentSlug.equals(doctor.getDepartment().getSlug()) &&
                                Boolean.TRUE.equals(doctor.isActive()))
                .toList();
    }

    @Override
    public List<Doctor> getAllActiveDoctors() {
        logger.info("Lấy tất cả bác sĩ đang hoạt động");
        return doctorRepository.findAll().stream()
                .filter(Doctor::isActive)
                .toList();
    }

    @Override
    public Doctor creaeDoctor(DoctorDTO request) {
        logger.info("Tạo bác sĩ với tên: {}", request.getFullName());

        String slug = SlugUtils.generateSlug(request.getFullName());
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
        if (request.getAvatarFile() != null && !request.getAvatarFile().isEmpty()) {
            avatarUrl = cloudinaryService.uploadFile(request.getAvatarFile());
        } else if (request.getAvatarUrl() != null && !request.getAvatarUrl().isBlank()) {
            avatarUrl = request.getAvatarUrl();
        }

        Boolean active = request.getIsActive();
        if (active == null) {
            active = true;
        }

        Doctor doctor = Doctor.builder()
                .fullName(request.getFullName())
                .slug(slug)
                .department(department)
                .description(request.getDescription())
                .avatarUrl(avatarUrl)
                .position(Position.valueOf(request.getPosition()))
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .isActive(active)
                .build();

        return doctorRepository.save(doctor);
    }

    @Override
    public Doctor updateDoctor(Long id, DoctorDTO request) {
        logger.info("Cập nhật bác sĩ với ID: {}", id);

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy bác sĩ với ID: {}", id);
                    return new RuntimeException("Không tìm thấy bác sĩ");
                });

        String newSlug;
        if (request.getSlug() != null && !request.getSlug().isBlank() && !request.getSlug().equals(doctor.getSlug())) {
            newSlug = request.getSlug();
        } else if (request.getFullName() != null && !request.getFullName().equals(doctor.getFullName())) {
            newSlug = SlugUtils.generateSlug(request.getFullName());
        } else {
            newSlug = doctor.getSlug();
        }

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

        String avatarUrl = doctor.getAvatarUrl(); // Preserve existing avatarUrl by default
        if (request.getAvatarFile() != null && !request.getAvatarFile().isEmpty()) {
            avatarUrl = cloudinaryService.uploadFile(request.getAvatarFile());
        } else if (request.getAvatarUrl() != null && !request.getAvatarUrl().isBlank()) {
            avatarUrl = request.getAvatarUrl();
        }

        Boolean active = request.getIsActive();
        if (active == null) {
            active = doctor.isActive();
        }

        if (request.getFullName() != null) {
            doctor.setFullName(request.getFullName());
        }
        doctor.setSlug(newSlug);
        if (department != null) {
            doctor.setDepartment(department);
        }
        if (request.getDescription() != null) {
            doctor.setDescription(request.getDescription());
        }
        doctor.setAvatarUrl(avatarUrl);
        doctor.setUpdateAt(LocalDateTime.now());
        if (request.getPosition() != null) {
            doctor.setPosition(Position.valueOf(request.getPosition()));
        }
        doctor.setActive(active);

        return doctorRepository.save(doctor);
    }

    @Override
    public void deleteDoctor(Long id) {
        logger.info("Xóa bác sĩ với ID: {}", id);

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy bác sĩ với ID: {}", id);
                    return new RuntimeException("Không tìm thấy bác sĩ");
                });

        doctorRepository.delete(doctor);
        logger.info("Đã xóa bác sĩ với ID: {}", id);
    }

    @Override
    public Doctor getDoctorById(Long id) {
        logger.info("Lấy bác sĩ với ID: {}", id);

        return doctorRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy bác sĩ với ID: {}", id);
                    return new RuntimeException("Không tìm thấy bác sĩ");
                });
    }

    @Override
    public Doctor getDoctorBySlug(String slug) {
        logger.info("Lấy bác sĩ với slug: {}", slug);

        return doctorRepository.findBySlug(slug)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy bác sĩ với slug: {}", slug);
                    return new RuntimeException("Không tìm thấy bác sĩ");
                });
    }

    @Override
    public void hideDoctor(Long id) {
        logger.info("Ẩn bác sĩ với ID: {}", id);
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy bác sĩ với ID: {}", id);
                    return new RuntimeException("Không tìm thấy bác sĩ");
                });

        doctor.setActive(!doctor.isActive());
        doctor.setUpdateAt(LocalDateTime.now());
        doctorRepository.save(doctor);
        logger.info("Đã ẩn bác sĩ với ID: {}", id);
    }

    @Override
    public List<String> getAllPositions() {
        logger.info("Lấy tất cả vị trí của bác sĩ");
        return List.of(Position.values()).stream()
                .map(Position::name)
                .toList();
    }
}