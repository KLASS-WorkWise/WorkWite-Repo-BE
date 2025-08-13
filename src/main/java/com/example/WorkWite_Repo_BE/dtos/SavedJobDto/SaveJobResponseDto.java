package com.example.WorkWite_Repo_BE.dtos.SavedJobDto;


import lombok.Builder;
import lombok.Data;

@Data

public class SaveJobResponseDto {
    private Long id;
    private Long jobId; // Chỉ trả về id công việc hoặc tên công việc
    private String savedAt;

    public SaveJobResponseDto(Long id,Long jobId, String savedAt) {
        this.id = id;
        this.jobId = jobId;
        this.savedAt = savedAt;
    }
}
