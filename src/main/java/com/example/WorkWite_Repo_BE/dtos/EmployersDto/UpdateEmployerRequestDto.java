package com.example.WorkWite_Repo_BE.dtos.EmployersDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmployerRequestDto {
//    @NotBlank(message = "Username không được để trống")
//    @Size(min = 4, max = 50, message = "Username phải từ 4 đến 50 ký tự")
//    private String username;


    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "\\d{1,15}", message = "Số điện thoại chỉ chứa số và tối đa 15 ký tự")
    private String phoneNumber;

    @Pattern(regexp = "^(https?|ftp)://.*$", message = "Avatar phải là URL hợp lệ")
    private String avatar;

    @NotBlank(message = "Tên không được để trống")
    private String fullName;

//    private String status;
}