package com.example.WorkWite_Repo_BE.dtos.ResumeDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdataResumeRequestDto {
    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "\\d{1,15}", message = "Số điện thoại chỉ chứa số và tối đa 15 ký tự")
    private String phone;

    @NotBlank(message = "Avatar không được để trống")
    @Pattern(regexp = "^(https?|ftp)://.*$", message = "Avatar phải là URL hợp lệ")
    private String profilePicture;

    @NotBlank(message = "Tóm tắt không được để trống")
    private String summary;

    @NotBlank(message = "Vị trí công việc không được để trống")
    private String jobTitle;
}
