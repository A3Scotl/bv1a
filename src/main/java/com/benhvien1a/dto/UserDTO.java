package com.benhvien1a.dto;

import com.benhvien1a.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String email;
    private String fullName;
    private Role role;
    private boolean active;
}