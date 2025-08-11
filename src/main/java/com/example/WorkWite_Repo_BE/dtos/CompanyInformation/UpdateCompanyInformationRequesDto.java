package com.example.WorkWite_Repo_BE.dtos.CompanyInformation;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompanyInformationRequesDto {
    @Min(value = 1, message = "Số lượng nhân viên phải lớn hơn 0")
    private Integer employee;

    @NotBlank(message = "Tên công ty không được để trống")
    @Size(max = 255, message = "Tên công ty không được vượt quá 255 ký tự")
    private String companyName;

    @Size(max = 500, message = "Logo URL không được vượt quá 500 ký tự")
    private String logoUrl;

    @Size(max = 500, message = "Banner URL không được vượt quá 500 ký tự")
    private String bannerUrl;

    @Email(message = "Email không hợp lệ")
    @Size(max = 255, message = "Email không được vượt quá 255 ký tự")
    private String email;

    @Digits(integer = 15, fraction = 0, message = "Số điện thoại không hợp lệ")
    private Integer phone;

    @Size(max = 2000, message = "Mô tả không được vượt quá 2000 ký tự")
    private String description;

    private LocalDateTime lastPosted;

    @Size(max = 500, message = "Địa chỉ không được vượt quá 500 ký tự")
    private String address;

    @Size(max = 255, message = "Vị trí không được vượt quá 255 ký tự")
    private String location;

    @Size(max = 255, message = "Website không được vượt quá 255 ký tự")
    private String website;

    @Size(max = 255, message = "Ngành nghề không được vượt quá 255 ký tự")
    private String industry;
}
