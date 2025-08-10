package com.benhvien1a.service;

import com.benhvien1a.dto.DoctorDTO;
import com.benhvien1a.model.Doctor;
import com.benhvien1a.model.Position;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DoctorService {
    Page<Doctor> getAllDoctors(Pageable pageable);
    Page<Doctor> getAllActiveDoctors(Pageable pageable);
    Page<Doctor> getFilteredDoctors(String fullName, Boolean isActive, Long departmentId, Position position, Pageable pageable);
    Page<Doctor> getDoctorsByDepartmentSlug(String departmentSlug, Pageable pageable);
    Doctor getDoctorById(Long id);
    Doctor getDoctorBySlug(String slug);
    Doctor createDoctor(DoctorDTO request);
    Doctor updateDoctor(Long id, DoctorDTO request);
    void deleteDoctor(Long id);
    void hideDoctor(Long id);
    List<String> getAllPositions();
}