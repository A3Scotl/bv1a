package com.benhvien1a.service;


import com.benhvien1a.dto.DoctorDTO;
import com.benhvien1a.model.Doctor;

import java.util.List;

public interface DoctorService {
    List<Doctor> getAllDoctors();
    List<Doctor> getAllActiveDoctors();
    Doctor creaeDoctor(DoctorDTO request);
    Doctor updateDoctor(Long id, DoctorDTO request);
    void deleteDoctor(Long id);
    Doctor getDoctorById(Long id);
    Doctor getDoctorBySlug(String slug);
    void hideDoctor(Long id);
    List<Doctor> getDotorsByDepartmentSlug(String departmnetSlug);
    List<String> getAllPositions();
}
