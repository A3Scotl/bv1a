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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(readOnly = true)
    public Page<Doctor> getAllDoctors(Pageable pageable) {
        logger.info("Fetching all doctors with pagination");
        return doctorRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Doctor> getAllActiveDoctors(Pageable pageable) {
        logger.info("Fetching all active doctors with pagination");
        return doctorRepository.findByIsActiveTrue(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Doctor> getFilteredDoctors(String fullName, Boolean isActive, Pageable pageable) {
        logger.info("Fetching doctors with fullName: {} and isActive: {}", fullName, isActive);
        return doctorRepository.findByFullNameAndIsActive(fullName, isActive, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Doctor> getDoctorsByDepartmentSlug(String departmentSlug, Pageable pageable) {
        logger.info("Fetching doctors by department slug: {}", departmentSlug);
        return doctorRepository.findByDepartmentSlug(departmentSlug, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Doctor getDoctorById(Long id) {
        logger.info("Fetching doctor with ID: {}", id);
        return doctorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Doctor getDoctorBySlug(String slug) {
        logger.info("Fetching doctor with slug: {}", slug);
        return doctorRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Doctor not found with slug: " + slug));
    }

    @Override
    @Transactional
    public Doctor createDoctor(DoctorDTO request) {
        logger.info("Creating doctor with name: {}", request.getFullName());
        String slug = SlugUtils.generateUniqueSlug(request.getFullName(), doctorRepository::existsBySlug);

        Department department = request.getDepartmentId() != null
                ? departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found with ID: " + request.getDepartmentId()))
                : null;

        String avatarUrl = request.getAvatarFile() != null && !request.getAvatarFile().isEmpty()
                ? cloudinaryService.uploadFile(request.getAvatarFile())
                : request.getAvatarUrl();

        Doctor doctor = Doctor.builder()
                .fullName(request.getFullName())
                .slug(slug)
                .department(department)
                .description(request.getDescription())
                .avatarUrl(avatarUrl)
                .position(request.getPosition() != null ? Position.valueOf(request.getPosition()) : null)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        return doctorRepository.save(doctor);
    }

    @Override
    @Transactional
    public Doctor updateDoctor(Long id, DoctorDTO request) {
        logger.info("Updating doctor with ID: {}", id);
        Doctor doctor = getDoctorById(id);

        String newSlug = doctor.getSlug();
        if (request.getFullName() != null && !request.getFullName().equals(doctor.getFullName())) {
            newSlug = SlugUtils.generateUniqueSlug(request.getFullName(), doctorRepository::existsBySlug);
        }

        Department department = request.getDepartmentId() != null
                ? departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found with ID: " + request.getDepartmentId()))
                : doctor.getDepartment();

        String avatarUrl = request.getAvatarFile() != null && !request.getAvatarFile().isEmpty()
                ? cloudinaryService.uploadFile(request.getAvatarFile())
                : request.getAvatarUrl() != null ? request.getAvatarUrl() : doctor.getAvatarUrl();

        doctor.setFullName(request.getFullName() != null ? request.getFullName() : doctor.getFullName());
        doctor.setSlug(newSlug);
        doctor.setDepartment(department);
        doctor.setDescription(request.getDescription() != null ? request.getDescription() : doctor.getDescription());
        doctor.setAvatarUrl(avatarUrl);
        doctor.setPosition(request.getPosition() != null ? Position.valueOf(request.getPosition()) : doctor.getPosition());
        doctor.setIsActive(request.getIsActive() != null ? request.getIsActive() : doctor.getIsActive());
        doctor.setUpdateAt(LocalDateTime.now());

        return doctorRepository.save(doctor);
    }

    @Override
    @Transactional
    public void deleteDoctor(Long id) {
        logger.info("Deleting doctor with ID: {}", id);
        Doctor doctor = getDoctorById(id);
        doctorRepository.delete(doctor);
    }

    @Override
    @Transactional
    public void hideDoctor(Long id) {
        logger.info("Toggling active status for doctor with ID: {}", id);
        Doctor doctor = getDoctorById(id);
        doctor.setIsActive(!doctor.getIsActive());
        doctor.setUpdateAt(LocalDateTime.now());
        doctorRepository.save(doctor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllPositions() {
        logger.info("Fetching all doctor positions");
        return List.of(Position.values()).stream()
                .map(Position::name)
                .toList();
    }
}