package com.example.WorkWite_Repo_BE.dtos.JobPostDto;

import lombok.Data;
import java.util.List;

@Data
public class JobPostingPaginatedDTO {
    private List<JobPostingResponseDTO> jobs;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
