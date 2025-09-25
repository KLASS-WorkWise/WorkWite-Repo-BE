package com.example.WorkWite_Repo_BE.dtos.ResumeDto;

import lombok.Data;
import java.util.List;

@Data
public class PaginatedResumeResponseDto {
    private List<ResumeResponseDto> data;
    private int pageNumber;
    private int pageSize;
    private long totalRecords;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
}
