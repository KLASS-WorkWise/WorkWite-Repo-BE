package com.example.WorkWite_Repo_BE.dtos.EmployersDto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpgradeEmployerRequestDto {
    @Min(value = 1, message = "Số lượng nhân viên min phải lớn hơn 0")
    private Integer minEmployee;

    private Integer maxEmployee;

    @NotBlank(message = "Tên công ty không được để trống")
    @Size(max = 255, message = "Tên công ty không được vượt quá 255 ký tự")
    private String companyName;

    @Size(max = 500, message = "Logo URL không được vượt quá 500 ký tự")
    private String logo;

    @Size(max = 500, message = "Banner URL không được vượt quá 500 ký tự")
    private String banner;

    @Email(message = "Email không hợp lệ")
    @Size(max = 255, message = "Email không được vượt quá 255 ký tự")
    private String email;

    @Pattern(regexp = "\\d{1,15}", message = "Số điện thoại không hợp lệ, chỉ chứa tối đa 15 chữ số")
    private String phone;

    @Size(max = 2000, message = "Mô tả không được vượt quá 2000 ký tự")
    private String description;

    @Size(max = 500, message = "Địa chỉ không được vượt quá 500 ký tự")
    private String address;

    @Size(max = 255, message = "Vị trí không được vượt quá 255 ký tự")
    private String location;

    @Size(max = 255, message = "Website không được vượt quá 255 ký tự")
    private String website;

    @Size(max = 255, message = "Ngành nghề không được vượt quá 255 ký tự")
    private String industry;

    private  String status;
}
