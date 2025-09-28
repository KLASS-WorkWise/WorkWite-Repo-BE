package com.example.WorkWite_Repo_BE.dtos.applicant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PreviewResponseDto {
    private Long resumesId;

    private double skillMatchPercent; // chỉ hiển thị % match
}
