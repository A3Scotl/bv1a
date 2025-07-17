package com.benhvien1a.model;

public enum Position {
    // Vị trí chuyên môn
    DOCTOR("Bác sĩ"),
    SURGEON("Bác sĩ phẫu thuật"),
    SPECIALIST("Bác sĩ chuyên khoa"),
    RADIOLOGIST("Bác sĩ chẩn đoán hình ảnh"),
    PHARMACIST("Dược sĩ"),
    LAB_TECHNICIAN("Kỹ thuật viên xét nghiệm"),
    NURSE("Điều dưỡng");
    private final String displayName;

    Position(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
