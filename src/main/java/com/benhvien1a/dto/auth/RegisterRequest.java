/*
 * @ (#) RegisterRequest.java 1.0 7/11/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved
 */
package com.benhvien1a.dto.auth;

import com.benhvien1a.model.Role;
import jakarta.validation.constraints.*;
import lombok.*;

/*
 * @description: DTO for admin to register a new user
 * @author: Nguyen Truong An
 * @date: 7/11/2025
 * @version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Email là bắt buộc")
    @Email(message = "Định dạng email không hợp lệ")
    private String email;

    @NotBlank(message = "Họ tên là bắt buộc")
    @Size(min = 2, max = 100, message = "Họ tên phải từ 2 đến 100 ký tự")
    private String fullName;

    @NotBlank(message = "Mật khẩu là bắt buộc")
    @Size(min = 8, message = "Mật khẩu phải dài ít nhất 8 ký tự")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Mật khẩu phải chứa chữ hoa, chữ thường, số và ký tự đặc biệt")
    private String password;

    private Role role;
}