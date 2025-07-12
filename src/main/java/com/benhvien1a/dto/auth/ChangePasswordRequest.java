/*
 * @ (#) ChangePasswordRequest.java 1.0 7/11/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved
 */
package com.benhvien1a.dto.auth;

import jakarta.validation.constraints.*;
import lombok.*;

/*
 * @description: DTO for admin to change user password
 * @author: Nguyen Truong An
 * @date: 7/11/2025
 * @version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
    @NotBlank(message = "Email là bắt buộc")
    @Email(message = "Định dạng email không hợp lệ")
    private String email;

    @NotBlank(message = "Mật khẩu là bắt buộc")
    @Size(min = 8, message = "Mật khẩu phải dài ít nhất 8 ký tự")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Mật khẩu phải chứa chữ hoa, chữ thường, số và ký tự đặc biệt")
    private String newPassword;
}